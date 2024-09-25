package com.example.demo1228_2.controller;


import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.example.demo1228_2.config.R;
import com.example.demo1228_2.config.Tool;
import com.example.demo1228_2.dto.BuylistDto;
import com.example.demo1228_2.entity.Action;
import com.example.demo1228_2.entity.Comment;
import com.example.demo1228_2.entity.Product;
import com.example.demo1228_2.entity.User;
import com.example.demo1228_2.mapper.ActionMapper;
import com.example.demo1228_2.mapper.CommentMapper;
import com.example.demo1228_2.mapper.ProductMapper;
import com.example.demo1228_2.mapper.UserMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author yjz
 * @since 2024-09-19
 */
@RestController
@Slf4j
@RequestMapping("/action")
public class ActionController {

    /**
     * 获取zan guanzhu comment的未读数
     * @param session 1
     * @return map
     */
    @GetMapping("/count")
    public  R GetCount(HttpSession session){
        Long id = Tool.getUserSessionId(session);
        Map<String,String> map = new HashMap<>();
        map.put("zan",Db.lambdaQuery(Action.class)
                .eq(Action::getReceiver_id,id)
                .eq(Action::getIs_read,false)
                .and(wrapper -> wrapper.eq(Action::getType, "zan")
                        .or()
                        .eq(Action::getType, "product_zan"))
                .count()
                .toString());
        map.put("guanzhu",Db.lambdaQuery(Action.class)
                .eq(Action::getReceiver_id,id)
                .eq(Action::getIs_read,false)
                .eq(Action::getType,"guanzhu")
                .count()
                .toString());
        map.put("comment",Db.lambdaQuery(Action.class)
                .eq(Action::getReceiver_id,id)
                .eq(Action::getIs_read,false)
                .and(wrapper -> wrapper.eq(Action::getType, "comment")
                        .or()
                        .eq(Action::getType, "product_comment"))
                .count()
                .toString());
        return R.success(map);
    }

    @Autowired
    CommentMapper commentMapper;
    @Autowired
    ProductMapper productMapper;

    @Autowired
    ObjectMapper objectMapper;


    /**
     * 获取未读的page of list
     * @param unread 是否获取未读的
     * @param currentPage 1
     * @param pageSize 1
     * @param setToRead 是否更新未读为已读
     * @param type zan guanzhu comment三选一
     * @param session 1
     * @return R
     */
    @GetMapping("/get")
    public R Get(@RequestParam Boolean unread,
                 @RequestParam Long currentPage,
                 @RequestParam Long pageSize,
                 @RequestParam Boolean setToRead,
                 @RequestParam String type,
                 HttpSession session){
        Long id = Tool.getUserSessionId(session);
        Page page = new Page<>(currentPage,pageSize);
        // 获取未读的
        if(unread){
            LambdaQueryChainWrapper<Action> query = Db.lambdaQuery(Action.class)
                    .eq(Action::getReceiver_id,id)
                    .eq(Action::getIs_read,false);
            // zan 获取多点赞文章
            if (type.equals("zan")) {
                query.and(wrapper -> wrapper.eq(Action::getType, "zan")
                        .or()
                        .eq(Action::getType, "product_zan"));
            } else if (type.equals("comment")) {
                query.and(wrapper -> wrapper.eq(Action::getType, "comment")
                        .or()
                        .eq(Action::getType, "product_comment"));
            }else{
                query.eq(Action::getType, type);
            };

            query.orderByDesc(Action::getCreate_time);
            page = query.page(page);
            // 设置未读为已读
            if (setToRead) {
                // 如果type是多种情况，需要处理多条件
                LambdaUpdateChainWrapper<Action> query2 = Db.lambdaUpdate(Action.class)
                        .eq(Action::getReceiver_id, id)
                        .eq(Action::getIs_read, false);  // 只更新未读的

                // zan 获取多点赞文章
                if (type.equals("zan")) {
                    query2.and(wrapper -> wrapper.eq(Action::getType, "zan")
                            .or()
                            .eq(Action::getType, "product_zan"));
                } else if (type.equals("comment")) {
                    query2.and(wrapper -> wrapper.eq(Action::getType, "comment")
                            .or()
                            .eq(Action::getType, "product_comment"));
                }else{
                    query2.eq(Action::getType, type);
                };

                query2.set(Action::getIs_read, true).update();
            }
        }else{
        // 获取已读
            LambdaQueryChainWrapper<Action> query = Db.lambdaQuery(Action.class)
                    .eq(Action::getReceiver_id,id)
                    .eq(Action::getIs_read,true);
            // zan 获取多点赞文章
            if (type.equals("zan")) {
                query.and(wrapper -> wrapper.eq(Action::getType, "zan")
                        .or()
                        .eq(Action::getType, "product_zan"));
            } else if (type.equals("comment")) {
                query.and(wrapper -> wrapper.eq(Action::getType, "comment")
                        .or()
                        .eq(Action::getType, "product_comment"));
            }else{
                query.eq(Action::getType, type);
            };
            query.orderByDesc(Action::getCreate_time);
            page = query.page(page);
        }
        // 给前端一些实时数据 setUser setBiao_obj set
        VOJSONAddForm(page);

        return R.success(page);
    }

    @Autowired
    UserMapper userMapper;
    // 本人动态
    @GetMapping("/get/dongtai")
    public R GetDongtai(@RequestParam Long currentPage, @RequestParam Long pageSize,HttpSession session){
        Long id = Tool.getUserSessionId(session);
        Page page = new Page<>(currentPage,pageSize);
        page = Db.lambdaQuery(Action.class)
                .eq(Action::getUser_id,id)
                .orderByDesc(Action::getCreate_time)
                .page(page);
        // 加些数据给前端
        VOJSONAddForm(page);

        return R.success(page);
    }


    /**
     * 添加 Biao_obj ，obj_biao_user，obj_biao_receiver，user，receiver
     * @param page 1
     */
    private void VOJSONAddForm(Page page) {
        page.setRecords(page.getRecords().stream().map(theobj -> {
            Action action = objectMapper.convertValue(theobj, new TypeReference<Action>() {});
            Long biao_id = action.getBiao_id();
            Long product_id=null;
            // setBiao_obj
            if(!ObjectUtils.isEmpty(biao_id)){
                if(action.getBiao().equals("t_comment")) {
                    Comment comment = commentMapper.selectById(biao_id);
                    action.setBiao_obj(objectMapper.convertValue(comment, new TypeReference<Map<String, Object>>() {}));
                    product_id = comment==null?null:comment.getProduct_id();
                }
            }

            Map<String,Object> obj = objectMapper.convertValue(action, new TypeReference<Map<String, Object>>() {});

            // Biao_id_user -> obj_biao_user
            if(!ObjectUtils.isEmpty(action.getBiao_id_user())) {
                if (action.getBiao().equals("t_comment")) {
                    Comment comment = commentMapper.selectById(action.getBiao_id_user());
                    obj.put("obj_biao_user", objectMapper.convertValue(comment, new TypeReference<Map<String, Object>>() {}));
                    product_id = comment==null?null:comment.getProduct_id();
                }
                if (action.getBiao().equals("t_product")){
                    Product product = productMapper.selectById(action.getBiao_id_receiver());
                    obj.put("obj_biao_user", objectMapper.convertValue(product, new TypeReference<Map<String, Object>>() {}));
                    product_id = product==null?null:product.getId();
                }
            }

            // Biao_id_receiver -> obj_biao_receiver
            if(!ObjectUtils.isEmpty(action.getBiao_id_receiver())){
                if(action.getBiao().equals("t_comment"))
                    obj.put("obj_biao_receiver",objectMapper.convertValue(commentMapper.selectById(action.getBiao_id_receiver()), new TypeReference<Map<String, Object>>() {}));
                if(action.getBiao().equals("t_product"))
                    obj.put("obj_biao_receiver",objectMapper.convertValue(productMapper.selectById(action.getBiao_id_receiver()), new TypeReference<Map<String, Object>>() {}));
            }

            obj.put("user",userMapper.selectById(action.getUser_id()));
            obj.put("receiver",userMapper.selectById(action.getReceiver_id()));
            obj.put("product_id",product_id!=null?product_id.toString():null);
            obj.put("product_name",productMapper.selectById(product_id)!=null?productMapper.selectById(product_id).getName():null);

            return obj;
        }).toList());
    }



}
