package com.example.demo1228_2.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.example.demo1228_2.config.CustomException;
import com.example.demo1228_2.component.GlobalProperties;
import com.example.demo1228_2.config.R;
import com.example.demo1228_2.config.Tool;
import com.example.demo1228_2.entity.*;
import com.example.demo1228_2.mapper.*;
import com.example.demo1228_2.service.impl.ProductServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.example.demo1228_2.config.Tool.PHOTO_SAVE_URL;

@RestController
@RequestMapping("/product")
@Slf4j // 自动生成log对象
public class ProductController {
    @Autowired
    ProductMapper productmapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    DataResultMapper dataResultMapper;

    @Autowired
    ProductRateMapper productRateMapper;

    @Autowired
    GlobalProperties globalProperties;

    @Autowired
    ObjectMapper objectMapper;

    @GetMapping("/page") // 分页查询 接收params //防空设默认
    public R<Page> FindPageProduct(@RequestParam Map<String,String> params,HttpSession session){
        //log.info("!!:{}",FType);
        try {

            int currentPage = Integer.parseInt(params.get("currentPage"));
            int PageSize = Integer.parseInt(params.get("PageSize"));
            String FName = params.get("FName");
            String FType = params.get("FType");
            String value2 = params.get("value2");

            LambdaQueryChainWrapper<Product> query = new LambdaQueryChainWrapper<>(productmapper);

            // 空参数抛异常
            if(currentPage == 0 || PageSize == 0 )throw new CustomException("分页查询参数为空");
            if(FName!=null && FName.length()>7)throw new CustomException("查询参数过长");
            // 分页查询
            Page page = new Page<>(currentPage, PageSize);

            // FName不为空 筛选名字
            if(FName != null)query.like(Product::getName,FName);
            // FType不为空 筛选种类
            if(FType != null)query.eq(Product::getType,FType);
            // 不分类时（在主页）只展示主页允许展示的
            else query.eq(Product::getIs_on_homepage,true);

            // 只展示允许展示的
            query.eq(Product::getIs_show,true).notBetween(Product::getId,45698L,46000L);

            // 添加置顶产品优先排序条件
            query.orderByDesc(Product::getIs_top);

            if(value2 != null){
                PageQueryValue2(query, value2);
            }




            // 执行查询
            page = query.page(page);

            /*
            //控制台打印json
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(res);
            System.out.println(json);
            //
            */
            log.info("分页查询成功");
            // region 访客去重逻辑
            Object obj = session.getAttribute("Home_visitors");
            LocalDate today = LocalDate.now();
            DataResult dataResult = dataResultMapper.selectById(45698);
            if (obj == null || !obj.toString().equals(today.toString())) {
                session.setAttribute("Home_visitors", today);

                // 访客加1
                if (!Tool.IsUserAdmin(session)) {
                    dataResult.setHome_visitors(dataResult.getHome_visitors() + 1);
                    dataResultMapper.updateById(dataResult);
                }
            }
            // endregion

            // 加点参数
            List<Product> res = page.getRecords();
            List<Map<String,Object>> nres = res.stream().map(product -> {
                Map<String,Object> entry = new HashMap<>(objectMapper.convertValue(product, Map.class));
                entry.put("comment_num",Db.lambdaQuery(Comment.class)
                        .eq(Comment::getProduct_id,product.getId())
                        .count());
                return entry;
            }).collect(Collectors.toList());
            page.setRecords(nres);

            return R.success(page).add("home_visitors",dataResult.getHome_visitors());
        }catch(Exception e){
            log.info("分页查询失败：{}",e.getMessage());
            return R.error(e.getMessage());
        }


    }

    @GetMapping("/getone") // 查一个商品
    public R<Product> FindOneProduct(@RequestParam(defaultValue = "-1")Long id,HttpSession session){
        Product product;
        if(id ==-1)return R.error("id为空");
        try{
            product = productmapper.selectById(id);
            if(!product.getIs_show() && !Tool.IsUserAdmin(session)  && !Tool.getUserSessionId(session).equals(product.getUser_id()))return R.error("已被隐藏");

            if(product != null && !Tool.IsUserAdmin(session)){
                // 浏览量加一
                product.setVisited_num(product.getVisited_num()+1);
                productmapper.updateById(product);
            }
        }catch(Exception e){
            log.info("查一个商品失败：{}",e.getMessage());
            return R.error(e.getMessage());
        }

        User user = userMapper.selectById(product.getUser_id());
        if(user==null)user = new User();
        //log.info("{},{}",averageRate,productRateList.size());
        return R.success(product)
                .add("user_name",user.getName())
                .add("rate_num",product.getRate_num())
                .add("user_id",user.getId()!=null?user.getId().toString():null)
                .add("user_wechat_nickname",user.getWechat_nickname());
    }

    @GetMapping("/getalltye2") // 查所有相同type2商品
    public R<String> FindOneProduct2(@RequestParam Map<String,String> params){
        String type2 = params.get("type2");
        if(type2==null)return R.success("无type2参数");

        List<Map<String,String>> rl = Db.lambdaQuery(Product.class)
                .eq(Product::getType2,type2)
                .list()
                .stream()
                .map(product -> Map.of(product.getId().toString(),product.getName()))
                .collect(Collectors.toList());

        return R.success("success").add("type2_list",rl);
    }

    /**
     * 新增一个赞
     * @param params comment_id
     * @param session 1
     * @return 1
     */
    @PostMapping("/addone/zan") // 加一个
    public R<String> FindOneProduct(@RequestBody Map<String,Object> params, HttpSession session){
        Long product_id;
        Long user_id;
        log.info("!!!!!"+params.toString());
        try{
            if(Tool.getUserSessionId(session).equals(0L))
                throw new CustomException("未登录");
            user_id = Long.parseLong(session.getAttribute("IsLogin").toString());
            product_id = Long.parseLong(params.get("product_id").toString());
        }catch (Exception e){
            return R.error(e.getMessage());
        }
        Product product = productmapper.selectById(product_id);
        List<Long> loveList = product.getLove_list();
        if (loveList == null) { // 防止空指针异常
            loveList = new ArrayList<>();
        }
        // 点赞过了 取消点赞
        if(loveList.contains(user_id)){
            loveList.remove(user_id);
            product.setLove_list(loveList);
            //comment.setLove_list_num(comment.getLove_list_num()-1);
            if(productmapper.updateById(product)!=1)
                return R.error("取消点赞失败，建议重试");
            return R.success("取消点赞成功");
            // 没点赞过 点赞
        }else{
            loveList.add(user_id);
            product.setLove_list(loveList);
            //comment.setLove_list_num(comment.getLove_list_num()+1);
            if(productmapper.updateById(product)!=1)
                return R.error("点赞失败，建议重试");
            return R.success("点赞成功");
        }

    }

    @Autowired
    TagMapper tagMapper;

    /**
     * 添加商品
     * @param photo 图片
     * @param product_json product字符串对象
     */
    @PostMapping("/addonebyadmin") // 加一个商品 //不加注解默认form-data
    public R<String> FindOneProduct(MultipartFile photo,MultipartFile photo_shot,String product_json,HttpSession session){
        try{

            //log.info(photo.getOriginalFilename());
            //log.info(photo.getContentType());
            //log.info(product_json);
            ObjectMapper objectMapper = new ObjectMapper();
            // 序列化
            Product product = objectMapper.readValue(product_json, Product.class);
            Map<String,Object> obj = objectMapper.readValue(product_json, new TypeReference<Map<String,Object>>() {});
            log.info("{}",product);
            // 反序列化
            //String json_str = objectMapper.writeValueAsString(product);
            //log.info(json_str);




            // 验证权限
            if(!session.getAttribute("Role").toString().equals("admin"))
                throw new CustomException("不是管理员，禁止操作");

            // 防空
            if(product.getName()==null )
                throw new CustomException("标题不能为空");

            // 防空
            if(product.getType()==null )
                throw new CustomException("分类不能为空");

            //名字防重复
            if(Db.lambdaQuery(Product.class).eq(Product::getName,product.getName()).one()!=null)
                throw new CustomException("名字已存在，添加失败");


            // 存图
            //Tool.saveFile(photo,Tool.PHOTO_SAVE_URL);
            // 转换webp存图
            String name = "noproduct";
            String name_shot = null;
            if(photo!=null)name = Tool.convertToWebp(photo,"");
            if(photo_shot!=null)name_shot = Tool.convertToWebp(photo_shot,"");
            log.info(name.replace(".webp", ""));
            //log.info("是否有图片 名字：{}",Tool.checkFileExists(name));

            product.setId(null);
            product.setVersion(null);
            product.setCreate_time(null);
            product.setUpdate_time(LocalDateTime.now());
            product.setUser_id(Long.parseLong(session.getAttribute("IsLogin").toString()));
            if(product.getType()==null)product.setType("0");
            product.setPhoto(name.replace(".webp", ""));
            if(name_shot!=null)product.setPhoto_shot(name_shot.replace(".webp", ""));
            if(productmapper.insert(product)==0){
                // 指定要删除的文件路径
                String filePath = PHOTO_SAVE_URL + name; // 替换为实际的文件路径
                File file = new File(filePath);
                // 尝试删除文件
                boolean deleted = file.delete();
                throw new CustomException("输入库插入失败");
            }else{
                boolean moveresult = false;
                if(!name.equals("noproduct"))
                    moveresult = Tool.moveFile(name,product.getId().toString()+'/'+name);
                log.info("图片移动结果："+moveresult);

                // 处理标签
                handleBiaoian(obj,product);

                return R.success("插入成功");
            }

        }catch(Exception e){
            return R.error(e.getMessage());
        }
    }

    @PostMapping("/saveTempPhotobyadmin") //保存临时图片
    public R<String> FindOneProduct(MultipartFile photo,HttpSession session){
        try{
            // 验证权限
            if(!session.getAttribute("Role").toString().equals("admin"))
                throw new CustomException("不是管理员，禁止操作");

            // 存图
            String name = "";
            if(photo!=null){
                name = Tool.convertToWebpAndSaveToPath("temp/",photo,"");
                //Tool.moveFile(name,product.getId().toString()+'/'+name);
            }else{
                throw new CustomException("图片为空");
            }
            log.info(name);
            return R.success(name);

        }catch (Exception e){
            return R.error(e.getMessage());
        }

    }

    @DeleteMapping("/deleteTempPhotobyadmin") //删除临时文件夹temp和里面所有文件(进入前端页面时会axios触发)
    public void FindOneProduct(){
        if(Tool.deleteAllFilesInDirectory(PHOTO_SAVE_URL+"temp/"))
            log.info("删除temp文件夹所有文件成功");
        else
            log.error("删除temp文件夹失败");
    }

    @PutMapping("/updateonebyadmin") // 更新一个商品 //不加注解默认form-data
    public R<String> UpdateOneProduct(MultipartFile photo,MultipartFile photo_shot,String product_json,HttpSession session){
        try{

            ObjectMapper objectMapper = new ObjectMapper();
            // 序列化
            Product product = objectMapper.readValue(product_json, Product.class);
            //log.info("{}",product);
            // 反序列化
            //String json_str = objectMapper.writeValueAsString(product);
            //log.info(json_str);
            Map<String,Object> obj = objectMapper.readValue(product_json, new TypeReference<Map<String,Object>>() {});

            // 验证权限
            if(!session.getAttribute("Role").toString().equals("admin"))
                throw new CustomException("不是管理员，禁止操作");

            // 防空
            if(product.getName()==null)
                throw new CustomException("名不能为空");

            Product db_product = productService.getById(product.getId());

            // 改名了，就检查名字防重复
            //if(!db_product.getName().equals(product.getName()) && Db.lambdaQuery(Product.class).eq(Product::getName,product.getName()).one()!=null)
            //    throw new CustomException("名字已存在，添加失败");


            // 有删除（或者本身是noproduct）
            if(product.getPhoto().equals("noproduct")){

                // 无传图
                if(photo==null){
                    // 本身不是noproduct（前端删除请求）
                    if(!db_product.getPhoto().equals("noproduct"))
                        // 删老的图
                        Tool.deleteOneWebp(db_product.getId().toString()+'/'+db_product.getPhoto()+".webp");

                    // 本身是 啥也不做
                }
                // 有传图 // 传图必是noproduct
                else{
                    // 删老的图(不是noproduct.webp的话,别把默认图删了)
                    if(!db_product.getPhoto().equals("noproduct"))Tool.deleteOneWebp(db_product.getId().toString()+'/'+db_product.getPhoto()+".webp");
                    // 存新图
                    String new_photo_name = Tool.convertToWebp(photo,"");
                    // 移位置
                    boolean moveresult = false;
                    moveresult = Tool.moveFile(new_photo_name,product.getId().toString()+'/'+new_photo_name);
                    log.info("图片移动结果："+moveresult);
                    // 改名
                    product.setPhoto(new_photo_name.replace(".webp",""));

                    log.info("删 存 改名");
                }

            }
            // 图片名字是正常文字（啥也不做）

            product.setUpdate_time(LocalDateTime.now());

            // 略缩图
            String name_shot=null;
            if(photo_shot!=null)name_shot = Tool.convertToWebp(photo_shot,"");
            if(name_shot!=null)product.setPhoto_shot(name_shot.replace(".webp", ""));

            if(!productService.updateById(product))
                throw new CustomException("updateById失败");

            movePhotoFromTempToIdFolder(product.getContent(),product.getId().toString());

            // 处理标签
            handleBiaoian(obj,product);

            return R.success("更新成功");



        }catch(Exception e){
            return R.error(e.getMessage());
        }
    }

    private void handleBiaoian(Map<String,Object> obj,Product product){
        // 先处理是否有新标签（新标签不是数字，直接就是中文）
        List<Integer> tagList = new ArrayList<>();
        List<String> tagList_str = new ArrayList<>();
        if(!ObjectUtils.isEmpty(obj.get("t_tag_label")))
            tagList_str = objectMapper.convertValue(obj.get("t_tag_label"),new TypeReference<List<String>>() {} );
        DataResult dataResult = dataResultMapper.selectById(45698L);
        Map<String,String> biaoqian_map = dataResult.getBiaoqian_map();
        // 是否有新建的标签
        tagList = tagList_str.stream().map(tag->{
            // 不存在的标签 新建
            if(biaoqian_map.get(tag)==null){
                // 先搞出索引
                int index = biaoqian_map.size();
                int add = -1;
                do{
                    add++;
                    index+=add;
                }while(biaoqian_map.get(index+"")!=null);
                biaoqian_map.put(index+"",tag);
                // tagList_str替换成index
                return index;
            }else{
                return Integer.parseInt(tag);
            }
        }).toList();
        log.info("toList是否改变tagList_str原值："+tagList_str);
        log.info("tagList:"+tagList);
        dataResult.setBiaoqian_map(biaoqian_map);
        // 更新标签
        dataResultMapper.updateById(dataResult);



        // 先查询出所有符合 product_id 的 Tag 记录
        Long Id = product.getId();
        // 查询出所有符合 product_id 的 Tag 记录
        List<Tag> existingTags = Db.lambdaQuery(Tag.class)
                .eq(Tag::getProduct_id, Id)
                .list();

        // 将查询结果的 tag_int 转为 Set，方便后续判断
        Set<Integer> existingTagInts = existingTags.stream()
                .map(Tag::getTag_int)
                .collect(Collectors.toSet());

        // 将 tagList 也转为 Set，方便对比
        Set<Integer> newTagInts = new HashSet<>(tagList);

        // 1. 新增：遍历 tagList，找到数据库中不存在的 tag_int 并进行插入
        newTagInts.forEach(num -> {
            if (!existingTagInts.contains(num)) {
                Tag tag = new Tag();
                tag.setProduct_id(Id);
                tag.setTag_int(num);
                tagMapper.insert(tag);
            }
        });

        // 2. 删除：遍历数据库中的记录，找到 tagList 中不存在的 tag_int 并进行删除
        existingTags.forEach(tag -> {
            if (!newTagInts.contains(tag.getTag_int())) {
                // 删除对应的记录
                tagMapper.delete(
                        new QueryWrapper<Tag>()
                                .eq("product_id", Id)
                                .eq("tag_int", tag.getTag_int())
                );
            }
        });
    }

    /**
     * 自定义列表转webp图(废弃)
     * @return
     */
    @GetMapping("/photosToWebpbyadmin")
    public String esad(){
        try {
            List<String> list = new ArrayList<>(Arrays.asList(
                    "https://cdn-l-cyberpunk.cdprojektred.com/wallpapers/3840x2160/CNY_Artwork-zh-cn.jpg",
                    "https://w.wallhaven.cc/full/6d/wallhaven-6d6yy6.jpg",
                    "https://w.wallhaven.cc/full/gp/wallhaven-gpgddl.jpg",
                    "https://w.wallhaven.cc/full/1p/wallhaven-1py3r1.jpg",
                    "https://w.wallhaven.cc/full/qz/wallhaven-qzq5l7.jpg",
                    "https://w.wallhaven.cc/full/m3/wallhaven-m3eve8.jpg",
                    "https://w.wallhaven.cc/full/p9/wallhaven-p9pro3.jpg"
            ));
            return Tool.ListconvertToWebp(list,"homePagePhoto");
        }catch (Exception e){
            return e.getMessage();
        }

    }


    @Autowired
    ProductServiceImpl productService;

    /**
     * 删除商品
     * @param productList 要删除的商品列表
     */
    @PostMapping("/deletelistbyadmin") // 删一堆商品（废弃了？被all接口泛型取代）
    public R<String> FindOneProduct(@RequestBody List<Product> productList,HttpSession session) {
        try{

            // 验证权限
            if(!session.getAttribute("Role").toString().equals("admin"))
                throw new CustomException("不是管理员，禁止操作");

            // 防空
            if(productList==null)
                throw new CustomException("参数不能为空");

            List<Long> IdList = new ArrayList<>();
            for(Product product:productList)
                if(product.getId()!=null){
                    IdList.add(product.getId());
                    // 这是单独删除封面图 其实可以删除对应id文件夹下所有文件
                    //Tool.deleteOneWebp(product.getId().toString() + '/' + product.getPhoto()+".webp"); // 删图片
                    //Tool.deleteAllFilesInDirectory(PHOTO_SAVE_URL + product.getId());
                    //后面不回滚了再删吧
                }

            if(!productService.removeByIds(IdList))
                throw new CustomException("数据库删除失败，可能存在非法ID");

            // 删文章时删除其他表文章下的一切（t_tags）
            Db.lambdaUpdate(Tag.class)
                    .in(Tag::getProduct_id, IdList)
                    .remove();
            // 文章下的评论
            Db.lambdaUpdate(Comment.class)
                    .in(Comment::getProduct_id, IdList)
                    .remove();


            // 删图
            for(Long Id:IdList){
                // 删除对应id文件夹下所有文件
                Tool.deleteAllFilesInDirectory(PHOTO_SAVE_URL + Id.toString()+'/');
            }


            return R.success("删除成功");

        }catch (Exception e){
            return R.error(e.getMessage());
        }

    }

    @GetMapping("/selectpagebyadmin") // 分页查询 接收params //防空设默认
    public R<Page<Product>> FindPageProducts(@RequestParam Map<String, String> params, HttpSession session){
        // 使用LambdaQueryChainWrapper构建查询
        LambdaQueryChainWrapper<Product> query = new LambdaQueryChainWrapper<>(productmapper);

        // 根据条件动态添加查询条件
        if (params.get("name") != null) {
            query.like(Product::getName, params.get("name"));
        }
        // 单独处理startDate，如果存在则查询大于等于这个日期的记录
        if (params.get("startDate") != null) {
            query.ge(Product::getCreate_time, params.get("startDate")); // ge是“greater than or equal to”的缩写
        }
        // 单独处理endDate，如果存在则查询小于等于这个日期的记录
        if (params.get("endDate") != null) {
            query.le(Product::getCreate_time, params.get("endDate")); // le是“less than or equal to”的缩写
        }
        if (params.get("id") != null) {
            query.like(Product::getId, params.get("id"));
        }
        if (params.get("type2") != null) {
            query.like(Product::getType2, params.get("type2"));
        }
        // 排序方式
        if (params.get("value2") != null) {
            String value2 = params.get("value2");
            PageQueryValue2(query, value2);
        }

        Page<Product> page = new Page<>(1,10);
        // 防空参数
        if(params.get("currentPage")!=null && params.get("PageSize")!=null)
            page = new Page<>(Long.parseLong(params.get("currentPage")),Long.parseLong(params.get("PageSize")));
        // 执行分页查询
        Page<Product> result = query.page(page);
        List newRe = result.getRecords().stream().map(item->{
            Map<String,Object> new_item;
            new_item = objectMapper.convertValue(item,Map.class);
            new_item.put("user_name",userMapper.selectById(item.getUser_id())!=null?
                    userMapper.selectById(item.getUser_id()).getName():"");
            return new_item;
        }).collect(Collectors.toList());
        result.setRecords(newRe);

        // map返回筛选
        R<Page<Product>> response = R.success(result);
        response.setMap(params);

        return response;
    }


    @GetMapping("/guidang") // 归档
    public List<Map<String,Object>> FindPageProducts2(@RequestParam(defaultValue = "-1") int type,
                                                      @RequestParam(defaultValue = "") String is){
        List<Map<String,Object>> res;
        res = productmapper.selectIdNameTime3(type,is);
        res.stream().peek(item-> {
            item.put("id", item.get("id").toString());
        }).toList();
        return res;
    }


    private void PageQueryValue2(LambdaQueryChainWrapper<Product> query, String value2) {
        switch(value2){
            case "a":
                break;
            case "b":
                query.orderByDesc(Product::getCreate_time);
                break;
            case "c":
                query.orderByDesc(Product::getVisited_num);
                break;
            case "d":
                query.orderByDesc(Product::getSold_num);
                break;
            case "e":
                query.orderByDesc(Product::getRate);
                break;
            case "f":
                query.orderByAsc(Product::getRate);
                break;
            case "g":
                query.orderByDesc(Product::getLove_list);
                break;
            default:
                break;
        }
    }

    // 从正文捕获所有图片 移动到id文件夹
    private void movePhotoFromTempToIdFolder(String content,String product_id) {
        // 定义正则表达式
        String regex = ".*\\/img/([^\\s]+\\.webp)";

        // 编译正则表达式
        Pattern pattern = Pattern.compile(regex);

        // 创建匹配器
        Matcher matcher = pattern.matcher(content);

        // 创建列表存储结果
        List<String> matchedResults = new ArrayList<>();

        // 找到所有匹配项
        while (matcher.find()) {
            // 获取匹配的字符串
            String matchedString = matcher.group(1);
            Tool.moveFile("temp/"+matchedString,product_id+"/"+matchedString);
        }

    }
}
