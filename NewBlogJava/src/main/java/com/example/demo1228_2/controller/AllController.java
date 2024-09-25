package com.example.demo1228_2.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.example.demo1228_2.Vo.Address;
import com.example.demo1228_2.config.R;
import com.example.demo1228_2.config.Tool;
import com.example.demo1228_2.entity.Comment;
import com.example.demo1228_2.entity.Tag;
import com.example.demo1228_2.entity.Friend;
import com.example.demo1228_2.entity.User;
import com.example.demo1228_2.mapper.AllMapper;
import com.example.demo1228_2.mapper.CommentMapper;
import com.example.demo1228_2.mapper.FriendMapper;
import com.example.demo1228_2.mapper.TagMapper;
import com.example.demo1228_2.service.impl.ActionServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/all")
@Slf4j // 自动生成log对象
public class AllController {

    @Autowired
    AllMapper allMapper;

    /**
     * 获取某个表的某元素统计个数 并逆序排序(废弃了)
     * @param yuansu 1
     * @param biao 1
     * @return list
     */
    @GetMapping("/paiming/{yuansu}/{biao}")
    public R get(@PathVariable String yuansu,
                    @PathVariable String biao) {
        try{
            return R.success("allMapper.getYuanSuPaiMing(yuansu,biao)");
        }catch (Exception e){
            return R.error(e.getMessage());
        }

    }

    /**
     * 获取某个表的某元素统计个数 并逆序排序
     * @param yuansu 1
     * @param biao 1
     * @return list
     */
    @PostMapping("/paiming/{yuansu}/{biao}")
    public R get3(@PathVariable String yuansu,
                 @PathVariable String biao,@RequestBody Map<String, Object> body, HttpSession session) {
        try{
            // t_tag_label单列
            if(yuansu.equals("t_tag_label"))return R.success(allMapper.selectcountTTagLabel());

            Map<String, List<Map<String, Object>>> yaunsuListMap=null;
            if(!ObjectUtils.isEmpty(body.get("yaunsuListMap"))) {
                yaunsuListMap = objectMapper.convertValue(body.get("yaunsuListMap"), new TypeReference<Map<String, List<Map<String, Object>>>>() {
                });
            }
            Long id= ObjectUtils.isEmpty(body.get("id"))?null:Long.parseLong(body.get("id").toString());
            String name = ObjectUtils.isEmpty(body.get("name"))?null:body.get("name").toString();
            String value2 = ObjectUtils.isEmpty(body.get("value2"))?null:body.get("value2").toString(); // 排序方式
            String search_text  = ObjectUtils.isEmpty(body.get("search_text"))?null:body.get("search_text").toString();
            LocalDateTime startDate = ObjectUtils.isEmpty(body.get("startDate"))?null:LocalDateTime.parse(body.get("startDate").toString());
            LocalDateTime endDate = ObjectUtils.isEmpty(body.get("endDate"))?null:LocalDateTime.parse(body.get("endDate").toString());

            List<Map<String,Object>> res = allMapper.selectGroupedData(yuansu, yaunsuListMap, biao, value2,name,startDate,endDate,search_text,id);
            // js的long精度丢失 转换为str给前端
            res.forEach(item->{
                if(item.get("yuansu") instanceof Long || item.get("yuansu") instanceof Integer)
                    item.put("yuansu",item.get("yuansu").toString());
            });
            return R.success(res);
        }catch (Exception e){
            return R.error(e.getMessage());
        }

    }

    @Autowired
    ObjectMapper objectMapper;

    @PostMapping("/selectpagebyadminWithSearch_SetList") // 泛查询 不定类型和表
    public R FindPageProducts2(@RequestBody Map body, HttpSession session){
        try {
            log.info(body.toString());

            Map yaunsuListMap = ObjectUtils.isEmpty(body.get("yaunsuListMap"))?null:(Map)body.get("yaunsuListMap");
            Long id= ObjectUtils.isEmpty(body.get("id"))?null:Long.parseLong(body.get("id").toString());
            List<Integer> tagList = ObjectUtils.isEmpty(body.get("tagList"))?null:(List<Integer>)body.get("tagList");
            String biao = ObjectUtils.isEmpty(body.get("biao"))?null:body.get("biao").toString();
            String name = ObjectUtils.isEmpty(body.get("name"))?null:body.get("name").toString();
            String value2 = ObjectUtils.isEmpty(body.get("value2"))?null:body.get("value2").toString(); // 排序方式
            LocalDateTime startDate = ObjectUtils.isEmpty(body.get("startDate"))?null:LocalDateTime.parse(body.get("startDate").toString());
            LocalDateTime endDate = ObjectUtils.isEmpty(body.get("endDate"))?null:LocalDateTime.parse(body.get("endDate").toString());
            // 先默认一个数值
            Page page = new Page<>(1, 10);
            // 防空参数
            if (body.get("currentPage") != null && body.get("PageSize") != null)
                page = new Page<>(Long.parseLong(body.get("currentPage").toString()), Long.parseLong(body.get("PageSize").toString()));

            page = allMapper.selectpagebyadminWithSearch_SetList(page, yaunsuListMap, biao, value2,name,startDate,endDate ,id,tagList);
            // 手动序列化一些数值 比如1.处理时间字符串 2.Long的id转String  3.love_list字符串到列表
            pageStringJsonSerialize(biao,page);

            return R.success(page);
        }catch (Exception e){
            return R.error(e.getMessage());
        }
    }

    @Autowired
    ActionServiceImpl actionService;

    @PutMapping("/updateonebyadmin/{biao}") // 泛更新
    public R put(@RequestBody Map object, @PathVariable String biao,HttpSession session) {
        try{
            if(!Tool.IsUserAdmin(session))return R.error("权限不足");
            objectForm(object);
            log.info(object+"");
            if(biao.equals("t_user")){
                // 密码哈希
                User user = objectMapper.convertValue(object, User.class);
                user.setPassword(Tool.encode(user.getPassword()));
                object = objectMapper.convertValue(user,Map.class);
            }
            int res = allMapper.updateonebyadmin(object,biao);
            if(res==1) {
                // 评论审核通过的话就action提醒
                if(biao.equals("t_comment") && (object.get("is_show").toString().equals("1") || object.get("is_show").toString().equals("true"))  ) {
                    Map<String,Object> params = new HashMap<>();
                    params.put("comment",object);
                    Comment comment = objectMapper.convertValue(object,Comment.class);
                    if(comment.getFather_comm_id().equals(0L)){
                        // 直接评论 无父
                        actionService.comment2(params);
                    }else{
                        // 回复某人
                        actionService.comment(params);
                    }

                }
                return R.success("");
            }
            else
                return R.error("数据库影响行为"+res);
        }catch (Exception e){
            return R.error(e.getMessage());
        }
    }

    @DeleteMapping("/deleteIdsbyadmin/{biao}") // 泛删除
    public R del(@RequestBody List<Long> Ids, @PathVariable String biao,HttpSession session) {
        try{
            if(!Tool.IsUserAdmin(session))return R.error("权限不足");
            log.info(Ids+"");
            int res = allMapper.deleteTByIds(Ids,biao);
            handleDeleteOtherTable(Ids,biao);
            if(res!=0)
                return R.success(res);
            else
                return R.error("数据库影响行为"+res);
        }catch (Exception e){
            return R.error(e.getMessage());
        }
    }

    /**
     * 删除其他表相关联的内容 比如 删除文章时删除评论表这个文章的所有评论
     * @param ids 1
     * @param biao 1
     */
    private void handleDeleteOtherTable(List<Long> ids, String biao) {
        if(biao.equals("t_product")){
            // 删文章时删除其他表文章下的一切（t_tags）
            Db.lambdaUpdate(Tag.class)
                    .in(Tag::getProduct_id, ids)
                    .remove();
            // 文章下的评论
            Db.lambdaUpdate(Comment.class)
                    .in(Comment::getProduct_id, ids)
                    .remove();
        }
    }

    @Autowired
    CommentMapper commentMapper;
    @Autowired
    FriendMapper friendMapper;
    @Autowired
    TagMapper tagMapper;

    @PostMapping("/insertonebyadmin/{biao}") // 泛增加(不会写)
    public R insert(@RequestBody Map object, @PathVariable String biao,HttpSession session) {
        try{
            if(!Tool.IsUserAdmin(session))return R.error("权限不足");

            int res = 0;
            if(biao.equals("t_comment")){
                res = commentMapper.insert(objectMapper.convertValue(object,Comment.class));
            }else if(biao.equals("t_friend")){
                res = friendMapper.insert(objectMapper.convertValue(object, Friend.class));
            }else{
                res = allMapper.insertonebyadmin(object,biao);
            }
            if(res==1)
                return R.success("");
            else
                return R.error("数据库影响行为"+res);
        }catch (Exception e){
            return R.error(e.getMessage());
        }
    }

    // 手动序列化一些数值 比如1.处理时间字符串 2.love_list字符串到列表
    private void pageStringJsonSerialize(String biao,Page page){
        page.setRecords(page.getRecords().stream().map(item->{
            try {
                Map<String, Object> map = objectMapper.convertValue(item, Map.class);
                // t_product表序列化 user_agent_details
                if (!ObjectUtils.isEmpty(map.get("create_time"))){
                    map.put("create_time", UTC0toUTC8(map.get("create_time").toString())) ;}
                if (!ObjectUtils.isEmpty(map.get("update_time")))
                    map.put("update_time", UTC0toUTC8(map.get("update_time").toString()));
                if (!ObjectUtils.isEmpty(map.get("last_visited_time")))
                    map.put("last_visited_time", UTC0toUTC8(map.get("last_visited_time").toString()));
                if (!ObjectUtils.isEmpty(map.get("id")))
                    map.put("id", map.get("id").toString());
                if (!ObjectUtils.isEmpty(map.get("user_id")))
                    map.put("user_id", map.get("user_id").toString());
                if (!ObjectUtils.isEmpty(map.get("love_list")))
                    map.put("love_list", objectMapper.readValue(map.get("love_list").toString(), new TypeReference<List<String>>() {}));
                if (!ObjectUtils.isEmpty(map.get("addresses")))
                    map.put("addresses", objectMapper.readValue(map.get("addresses").toString(), new TypeReference<List<Address>>() {}));
                // comment
                List<String> tostringList = new ArrayList<>(Arrays.asList(
                        "user_id","product_id","father_comm_id","replay_to_user_id"
                ));
                tostringList.forEach(s->{
                    if (!ObjectUtils.isEmpty(map.get(s)))
                        map.put(s, map.get(s).toString());
                });
                // 泛id value长度为19&&key包含id字符串 转成str丢给前端
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    if(!ObjectUtils.isEmpty(value) && value.toString().length()==19 && key.contains("id"))
                        map.put(key,value.toString());
                }
                // t_tag_label
                if (biao.equals("t_product") && !ObjectUtils.isEmpty(map.get("id"))) {
                    List<Integer> t_tag_label = tagMapper.selectUniqueTagIntByProductId(Long.parseLong(map.get("id").toString()));
                    map.put("t_tag_label",t_tag_label);
                }

                return map;
            } catch (Exception e) {
                // 处理异常，比如记录日志
                throw new RuntimeException( e);
            }
        }).toList());
    }

    private String UTC0toUTC8(String utcTimeString){
        // 解析UTC时间字符串为Instant对象
        Instant utcInstant = Instant.parse(utcTimeString);

        // 将Instant对象转换为东八区（Asia/Shanghai）时间的ZonedDateTime对象
        ZonedDateTime zonedDateTime = utcInstant.atZone(ZoneId.of("Asia/Shanghai"));

        // 格式化ZonedDateTime为字符串
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String localTimeString = zonedDateTime.format(formatter);
        return localTimeString.replace(" ","T");
    }

    /**
     * 手动反序列化 不定->string->Long ..
     * @param map 1
     * @throws Exception 1
     */
    private void objectForm(Map map)throws Exception{
        // comment表序列化

        //if (!ObjectUtils.isEmpty(map.get("love_list")))// 这个在xml里交给序列化器了
            //map.put("love_list", "objectMapper.writeValueAsString(map.get("love_list"))");
        // comment
        List<String> tostringList = new ArrayList<>(Arrays.asList(
                "id","user_id","product_id","father_comm_id","replay_to_user_id"
        ));
        tostringList.forEach(s->{
            if (!ObjectUtils.isEmpty(map.get(s)))
                map.put(s,  Long.parseLong(map.get(s).toString()));
        });
    }

    private void processMap(Map<String, Object> map,List<String> tostringList) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if ((tostringList.contains(key)||key.equals("value")) && value instanceof Map) {
                // 递归处理嵌套的Map
                processMap((Map<String, Object>) value,tostringList);
            } else if (value instanceof List) {
                // 递归处理嵌套的List
                processList((List<Object>) value,tostringList);
            } else if (!ObjectUtils.isEmpty(value)) {
                // 转换特定键的值为Long
                map.put(key, Long.parseLong(value.toString()));
            }
        }
    }

    private void processList(List<Object> list,List<String> tostringList) {
        for (Object element : list) {
            if (element instanceof Map) {
                // 递归处理List中的Map
                processMap((Map<String, Object>) element,tostringList);
            } else if (element instanceof List) {
                // 递归处理嵌套的List
                processList((List<Object>) element,tostringList);
            }
        }
    }



}
