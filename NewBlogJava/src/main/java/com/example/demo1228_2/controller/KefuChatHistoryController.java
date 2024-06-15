package com.example.demo1228_2.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.example.demo1228_2.config.CustomException;
import com.example.demo1228_2.component.GlobalProperties;
import com.example.demo1228_2.config.R;
import com.example.demo1228_2.config.Tool;
import com.example.demo1228_2.dto.OpenAiJsonMessageObject;
import com.example.demo1228_2.dto.OpenAiSendJson;
import com.example.demo1228_2.entity.KefuChatHistory;
import com.example.demo1228_2.entity.Product;
import com.example.demo1228_2.service.impl.HttpService;
import com.example.demo1228_2.service.impl.KefuChatHistoryServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author yjz
 * @since 2024-05-13
 */
@Slf4j
@RestController
@RequestMapping("/kefu")
public class KefuChatHistoryController {

    @Autowired
    HttpService httpService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    GlobalProperties globalProperties;

    @Autowired
    KefuChatHistoryServiceImpl kefuChatHistoryService;

    /**
     * 查询单个聊天 有则返回对话列表 chat_id 无则返回null
     * @return OpenAiSendJson
     */
    @GetMapping("/chat")
    public R<List<OpenAiJsonMessageObject>> testt2(@RequestParam String chat_id,HttpSession session){
        Long chat_id_L;
        try{
            chat_id_L = Long.parseLong(chat_id);
        }catch (Exception e){
            return R.error(e.getMessage());
        }

        List<OpenAiJsonMessageObject> message;
        KefuChatHistory kefuChatHistory;
        // 查最新
        if(chat_id_L == -1){
            kefuChatHistory = Db.lambdaQuery(KefuChatHistory.class)
                    .eq(KefuChatHistory::getUser_id, Tool.getUserSessionId(session))
                    .eq(KefuChatHistory::getIs_finish,false)
                    .one();
            // 不存在已发起且未结束聊天 返回null
            if(kefuChatHistory==null)
                return R.success(null);

        // 查指定id
        }else{
            kefuChatHistory = Db.lambdaQuery(KefuChatHistory.class)
                    .eq(KefuChatHistory::getUser_id, Tool.getUserSessionId(session))
                    .eq(KefuChatHistory::getId,chat_id_L)
                    .one();
            // 不存在已发起且未结束聊天 返回null
            if(kefuChatHistory==null)
                return R.error("该聊天不存在");
        }


        message = kefuChatHistory.getSend_json().getMessages();
        message.remove(0); // 删除系统提示词返回
        return R.success(message).add("chat_id",kefuChatHistory.getId().toString())
                .add("kefu_name",kefuChatHistory.getKefu_name()).add("is_finish",kefuChatHistory.getIs_finish());
    }

    /**
     * 客户聊天记录分页
     * @param session 1
     * @return 1
     */
    @GetMapping("/page")
    public R testt234(@RequestParam Map<String,String> params,HttpSession session) {
        Long user_id;
        int currentPage;
        int PageSize;
        try {
            user_id = Tool.getUserSessionId(session);
            currentPage = Integer.parseInt(params.get("currentPage"));
            PageSize = Integer.parseInt(params.get("PageSize"));
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
        Page page = new Page<>(currentPage, PageSize);

        return R.success(Db.lambdaQuery(KefuChatHistory.class)
                .eq(KefuChatHistory::getUser_id,user_id)
                .orderByDesc(KefuChatHistory::getCreate_time)
                .page(page));
    }

    /**
     * Openai 发起聊天请求
     * @param user_send_message_object 1
     * @return 1
     */

    @PostMapping("/chat")
    public R testt(@RequestBody OpenAiJsonMessageObject user_send_message_object,
                                        @RequestParam String chat_id, HttpSession session){
        // 这里！！记得限制每个用户的速率 请求次数。。 防止高频请求耗尽key 同时针对单个用户上redis并发锁（已在下面try前实现）
        // region 一些参数初始化

        // 创建空请求JSON
        OpenAiSendJson openAiSendJson = new OpenAiSendJson();
        // 选模型 gpt-3.5-turbo（如果没从数据库获取，新建的话）
        openAiSendJson.setModel("gpt-3.5-turbo");
        // 测试用的，获取客服名
        String kefu_name="";
        // 创建空对话列表
        List<OpenAiJsonMessageObject> openAiJsonMessageObjectList = new ArrayList<>();
        // endregion
        try {
            // region 1.设置一个redis锁 防止一个用户并发请求 造成对话创建重复 2.限制速率锁 防止高频消耗token
            try (Jedis jedis = new Jedis("localhost")) {
                jedis.auth(globalProperties.REDIS_SECRET);
                int limited_ttl_time = 10*60;   // 10分钟为速率限制（限制速率锁的参数）
                int count_limited = 100;         // 每10分钟限制30次访问（限制速率锁的参数）
                //1. 单个用户chat防并发锁
                if(jedis.get("chat_lock"+Tool.getUserSessionId(session))!=null)
                    return R.error("并发异常，请等待上一请求完成再发起");
                jedis.setex("chat_lock"+Tool.getUserSessionId(session), 30, ""); // 键“key”在30秒后过期
                //2. 限制速率锁
                // 2.1 已存在锁 未过期
                if(jedis.get("chat_user_count"+Tool.getUserSessionId(session))!=null
                        && jedis.ttl("chat_user_count"+Tool.getUserSessionId(session))>0){
                    // 获取上次访问次数
                    int count = Integer.parseInt(jedis.get("chat_user_count"+Tool.getUserSessionId(session)));
                    // 超过限制次数 抛异常
                    if(count>=count_limited)throw new CustomException("访问速率超限:"+count_limited+"in"+limited_ttl_time/60+"min");
                    // 获取过期时间
                    int ttl = Integer.parseInt(jedis.ttl("chat_user_count"+Tool.getUserSessionId(session)).toString());
                    // 访问次数++ 过期时间不改
                    jedis.setex("chat_user_count"+Tool.getUserSessionId(session),ttl,""+(++count));
                    // 2.2 不存在锁
                }else{
                    // 加锁 limited_ttl_time秒速率限制
                    jedis.setex("chat_user_count"+Tool.getUserSessionId(session),limited_ttl_time,"1");
                }

            } catch (Exception e) {
                log.info("redis异常: " + e.getMessage());
                return R.error("redis异常:"+ e.getMessage());
            }
            // endregion

            // region 校验客户user_send_message_object输入
            if(!user_send_message_object.getRole().equals("user"))
                throw new CustomException("角色错误");
            if(user_send_message_object.getContent().length()>=50)
                throw new CustomException("内容过长");
            if(user_send_message_object.getContent().length()==0)
                throw new CustomException("内容为空");
            if(chat_id!=null && !chat_id.equals("null") &&kefuChatHistoryService.getById(Long.parseLong(chat_id)).getIs_finish())
                throw new CustomException("此轮对话已结束");
            // endregion

            // region查询有无已发起且未结束聊天
            KefuChatHistory kefuChatHistory = Db.lambdaQuery(KefuChatHistory.class)
                    .eq(KefuChatHistory::getUser_id, Tool.getUserSessionId(session))
                    .eq(KefuChatHistory::getIs_finish,0)
                    .one();

            // 不存在已发起且未结束聊天
            if(kefuChatHistory==null){
                // 构造kefuChatHistory
                kefuChatHistory = new KefuChatHistory();
                kefuChatHistory.setUser_id(Tool.getUserSessionId(session));
                // 构造系统提示词对象(获取随机客服提示词)
                Map<String,Object> random_kefu_resultMap = get_random_kefu(-1);
                // 获取客服名
                kefu_name = random_kefu_resultMap.get("kefu_name").toString();
                kefuChatHistory.setKefu_name(kefu_name);
                OpenAiJsonMessageObject openAiJsonMessageObject = objectMapper.convertValue(random_kefu_resultMap.get("openAiJsonMessageObject"), OpenAiJsonMessageObject.class);
                // 头插入系统提示词
                openAiJsonMessageObjectList.add(0,openAiJsonMessageObject);
                // 再插入用户输入(先设定时间)
                user_send_message_object.setCreate_time(LocalDateTime.now());
                openAiJsonMessageObjectList.add(user_send_message_object);
                // 请求体构造消息列表
                openAiSendJson.setMessages(openAiJsonMessageObjectList);

            // 存在已发起且未结束聊天
            }else{
                // 直接获取请求体
                openAiSendJson = kefuChatHistory.getSend_json();
                // 获取客服名
                kefu_name = kefuChatHistory.getKefu_name();
                // 获取对话列表
                openAiJsonMessageObjectList = openAiSendJson.getMessages();
                // 大于等于10次抛异常(一般不可能出现Is_finish为true 但没设置)
                if(openAiJsonMessageObjectList.size()>=10)throw new CustomException("数据库数据异常");
                // 8次 + 这次问 + 返回答 = 10 所以 设置Is_finish为true(忘算提示词 8+1=9)
                if(openAiJsonMessageObjectList.size()==9)kefuChatHistory.setIs_finish(true);
                // 请求体构造消息列表
                user_send_message_object.setCreate_time(LocalDateTime.now());
                openAiJsonMessageObjectList.add(user_send_message_object);

            }
            // endregion

            // region匹配看有无匹配命令
            OpenAiJsonMessageObject openAiJsonMessageObject = fetchCommand(user_send_message_object,kefuChatHistory);
            if(openAiJsonMessageObject!=null){
                // 有匹配 返回响应消息体
                openAiSendJson.getMessages().add(openAiJsonMessageObject);
                kefuChatHistory.setSend_json(openAiSendJson);
            }else{
                // 无匹配 请求gpt
                // region提交GPT的post请求 // 接收响应体
                log.info(""+openAiSendJson);
                Map<String,String> http_result = httpService.sendPostToOpenAI(globalProperties.OPENAI_KEY
                        ,objectMapper.writeValueAsString(openAiSendJson));
                // 返回码不为200 表示错误 抛异常
                if(!http_result.get("statusCode").equals("200"))
                    throw new CustomException("openai返回错误:"+http_result.get("body"));
                Map<String,Object> body = objectMapper.readValue(http_result.get("body"),Map.class);
                // 解析响应体
                List<Object> list = (List<Object>) body.get("choices");
                Map<String, Object> map = (Map<String, Object>) list.get(0);
                Map<String, Object> answer = (Map<String, Object>) map.get("message");
                // 检查有无触发指令
                //check_if_command(answer,kefuChatHistory);
                // sendjson加上gpt返回的对话Object
                openAiJsonMessageObject = objectMapper.convertValue(answer,OpenAiJsonMessageObject.class);
                openAiJsonMessageObject.setCreate_time(LocalDateTime.now());
                openAiSendJson.getMessages().add(openAiJsonMessageObject);
                kefuChatHistory.setSend_json(openAiSendJson);
                // endregion
            }
            // endregion

            // region数据库更新(或创建)kefuChatHistory
            // 创建
            if(kefuChatHistory.getId()==null){
                kefuChatHistory.setIs_finish(false);
                if(!kefuChatHistoryService.save(kefuChatHistory))
                    throw new CustomException("数据库创建kefuChatHistory失败");
            // 更新
            }else{
                if(!kefuChatHistoryService.updateById(kefuChatHistory))
                    throw new CustomException("数据库更新kefuChatHistory失败");
            }
            // endregion

            return R.success(openAiJsonMessageObject).add("kefu_name",kefuChatHistory.getKefu_name()).add("chat_id",kefuChatHistory.getId().toString())
                    .add("is_finish",kefuChatHistory.getIs_finish());


        }catch (Exception e){
            log.info(e.getMessage());
            return R.error(e.getMessage());
        }finally{
            // 删除防单用户并发锁
            try (Jedis jedis = new Jedis("localhost")) {
                jedis.auth(globalProperties.REDIS_SECRET);
                jedis.del("chat_lock"+Tool.getUserSessionId(session));
            } catch (Exception e) {
                log.info("redis异常: " + e.getMessage());
            }
        }
    }

    /**
     * 随机客服提示词 返回Map<String,Object> 1openAiJsonMessageObject 2kefu_name
     * @param select_randint -1是随机 其他是指定
     * @return 返回Map<String,Object> 1openAiJsonMessageObject 2kefu_name
     */
    private Map<String,Object> get_random_kefu(int select_randint){
        // 结果集Map
        Map<String,Object> resultMap = new HashMap<>();
        // 生成m-n-1位随机数 [n,m)
        int randint = ThreadLocalRandom.current().nextInt(0, 3);
        // 数字对应的客服名字
        List<String> kefu_name = Arrays.asList("Doge客服", "猫娘客服", "天才客服");
        // 随机构造系统提示词对象
        OpenAiJsonMessageObject openAiJsonMessageObject = new OpenAiJsonMessageObject();
        // 有无指定客服号
        if(select_randint!=-1)randint = select_randint;

        switch (randint) {
            case 0 -> { // doge商城客服
                openAiJsonMessageObject.setRole("system");
                openAiJsonMessageObject.setContent("你是一个Doge客服，不管别人问啥，你都只会汪汪汪（随机汪字数，符号，emoji）即兴发挥，" +
                        "呜呜呜，带上doge的emoji");
            }
            case 1 -> { // 猫娘电子商城客服
                openAiJsonMessageObject.setRole("system");
                openAiJsonMessageObject.setContent("你是一个猫娘电子商城客服，可爱并且每次对话结尾都会加上‘喵喵~’和自定义颜文字或者" +
                        "表情比如\uD83D\uDE04\uD83D\uDE0B\uD83D\uDE3C（除了系统命令对话结尾不加）");
            }
            default -> { // 傻子电子商城客服
                openAiJsonMessageObject.setRole("system");
                openAiJsonMessageObject.setContent("你是一个问啥只会回答不知道或我也不知道的傻子电子商城客服，但是要结合客户的问题回答不知道，比如客户问" +
                        "你知道商城卖啥，你要回答，关于商城卖啥，我也不知道，哈哈\uD83D，加上一些嘲笑的emoji比如\uD83D\uDE04（除了问老板是谁知道）");
            }
        }

        // 设置共同提示词
        String tsc = "，商城的主人老板叫阿西巴，是个大帅比。" ;//+
                //"第一，如果客人说要结束对话，你就只回答'system_operation:end_chat'作为系统命令，我会在后端正则匹配到，然后执行对应操作。" +
                //"第二，如果客人说，要查某件商品，就你就只回答'find_product:商品名'作为系统命令，我会在后端正则匹配到，然后执行对应操作，" +
                //"注意，由于我查找到商品后，会替换聊天记录中find_product:后的内容，比如你输入'find_product:食物'，我会查找到商品"+
               // "'食物'之后，替换掉原本聊天记录的'find_product:食物'变成形如':find_product:[{\\\"id\\\":\\\"1768894204353769473\\\",\\\"name\\\":\\\"美...'"+
               // "的具体后端查到的商品列表，但当客户再次让你查询商品时，你还是要输出形如'find_product:食物'的格式";
        openAiJsonMessageObject.setContent(openAiJsonMessageObject.getContent()+tsc);

        resultMap.put("openAiJsonMessageObject",openAiJsonMessageObject);
        resultMap.put("kefu_name",kefu_name.get(randint));

        return resultMap;
    }

    /**
     * 看看GPT有没有根据客人信息给指令 (随机性太强，我想在后端捕获处理)
     */
    private void check_if_command(Map<String, Object> answer,KefuChatHistory kefuChatHistory)throws Exception{
        // 结束对话指令
        if(answer.get("content").toString().contains("system_operation:end_chat")){
            kefuChatHistory.setIs_finish(true);
            answer.put("content","好的，已结束对话");
        }
        // 查找商品指令
        if(answer.get("content").toString().contains("find_product:")){
            String content = answer.get("content").toString();
            int index = content.indexOf("find_product:") + "find_product:".length();
            String productQuery = content.substring(index);
            //log.info("productQuery:"+productQuery);
            List<Product> list = Db.lambdaQuery(Product.class).like(Product::getName,productQuery)
                            .list();
            String json = objectMapper.writeValueAsString(list);
            answer.put("content",":find_product:"+json);
        }
    }

    /**
     * 匹配客户发来的字符串 能不能符合客服命令 符合直接返回结果 不经过gpt
     * @return openAiJsonMessageObject 返回回答
     */
    private OpenAiJsonMessageObject fetchCommand(OpenAiJsonMessageObject user_send_message_object,KefuChatHistory kefuChatHistory)throws Exception{
        // 客户输入的
        String content = user_send_message_object.getContent();
        // 回答构造体
        OpenAiJsonMessageObject openAiJsonMessageObject_answer = new OpenAiJsonMessageObject();
        openAiJsonMessageObject_answer.setRole("assistant");
        openAiJsonMessageObject_answer.setCreate_time(LocalDateTime.now());
        // region正则1 查找商品
        String regex = "(查找商品|有没有商品|查询商品)\\s*(.*?)(啊|\\s|$)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            String answer = matcher.group(2).trim(); // 捕获第一个分组，即商品名
            log.info("正则匹配到:"+answer);
            List<Product> list = Db.lambdaQuery(Product.class).like(Product::getName,answer)
                    .list();
            openAiJsonMessageObject_answer.setContent(":find_product:"+objectMapper.writeValueAsString(list));
            return openAiJsonMessageObject_answer;
        }
        // endregion

        // region正则2 是否结束聊天
        regex = ".*?(结束对话|结束聊天|退出对话|退出聊天).*?";
        pattern = Pattern.compile(regex);
        matcher = pattern.matcher(content);
        if (matcher.find()) {
            String answer = matcher.group(1).trim(); // 捕获第一个分组，即商品名
            log.info("正则匹配到:"+answer);
            openAiJsonMessageObject_answer.setContent("好的，已结束聊天");
            kefuChatHistory.setIs_finish(true);
            return openAiJsonMessageObject_answer;
        }
        // endregion

        // region正则3 是否切换客服
        regex = ".*(切换客服|换个客服)\\s*(\\d+)";
        pattern = Pattern.compile(regex);
        matcher = pattern.matcher(content);
        if (matcher.find()) {
            String answer = matcher.group(2).trim(); // 捕获第二个分组，即客服编号
            try {
                int kefuNumber = Integer.parseInt(answer);
                // 进行客服切换操作
                OpenAiJsonMessageObject openAiJsonMessageObject = kefuChatHistory.getSend_json().getMessages().get(0);
                Map res_map = get_random_kefu(kefuNumber);
                OpenAiJsonMessageObject newKefuMessageObject = objectMapper.convertValue(res_map.get("openAiJsonMessageObject"), OpenAiJsonMessageObject.class);
                openAiJsonMessageObject.setContent(newKefuMessageObject.getContent());
                kefuChatHistory.setKefu_name(res_map.get("kefu_name").toString());
                openAiJsonMessageObject_answer.setContent("好的，已切换" + answer + "号客服，下次回复生效");
                log.info("正则匹配到客服切换操作");
                return openAiJsonMessageObject_answer;
            } catch (NumberFormatException e) {
                openAiJsonMessageObject_answer.setContent("客服号无效");
                return openAiJsonMessageObject_answer;
            }
        }
        // endregion


        // 都没匹配 返回null 调用gpt
        return null;
    }

}
