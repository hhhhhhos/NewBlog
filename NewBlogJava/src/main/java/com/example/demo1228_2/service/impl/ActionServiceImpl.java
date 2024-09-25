package com.example.demo1228_2.service.impl;

import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.example.demo1228_2.config.R;
import com.example.demo1228_2.config.Tool;
import com.example.demo1228_2.entity.*;
import com.example.demo1228_2.mapper.*;
import com.example.demo1228_2.service.IActionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author yjz
 * @since 2024-09-19
 */
@Slf4j
@Service
public class ActionServiceImpl extends ServiceImpl<ActionMapper, Action> implements IActionService {


    @Autowired
    ActionMapper actionMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    CommentMapper commentMapper;
    @Autowired
    ProductMapper productMapper;

    @Autowired
    UserOptionMapper userOptionMapper;

    // 点赞评论 zan t_comment
    public void zan_comment(Map<String,Object> params)throws Exception{
        Long user_id = Long.parseLong(params.get("user_id").toString());
        Comment comment = (Comment) params.get("comment");
        Action action = new Action();

        action.setUser_id(user_id);
        //action.setUser(userMapper.selectById(user_id));
        action.setReceiver_id(comment.getUser_id());
        action.setType("zan");
        action.setBiao("t_comment");
        action.setBiao_id(comment.getId());
        //action.setBiao_obj(objectMapper.convertValue(comment,new TypeReference<Map<String, Object>>() {}));
        if(!Db.lambdaQuery(Action.class)
                .eq(Action::getUser_id,user_id)
                .eq(Action::getReceiver_id,comment.getUser_id())
                .eq(Action::getType,"zan")
                .eq(Action::getBiao_id,comment.getId())
                .exists())
            actionMapper.insert(action);
    }

    // 被关注 guanzhu t_fan
    public void guanzhu(Map<String,Object> params)throws Exception{

        Fan obj = (Fan) params.get("fan");
        Action action = new Action();

        action.setUser_id(obj.getUser_id());
        //action.setUser(userMapper.selectById(obj.getUser_id()));
        action.setReceiver_id(obj.getUser_id2());
        action.setType("guanzhu");
        action.setBiao("t_fan");
        action.setBiao_id(obj.getId());
        //action.setBiao_obj(objectMapper.convertValue(obj,new TypeReference<Map<String, Object>>() {}));
        if(!Db.lambdaQuery(Action.class)
                .eq(Action::getUser_id,obj.getUser_id())
                .eq(Action::getReceiver_id,obj.getUser_id())
                .eq(Action::getType,"guanzhu")
                .eq(Action::getBiao_id,obj.getId())
                .exists())
            actionMapper.insert(action);
    }

    // 回复评论(进入这里，Father_comm_id必是有的) comment t_comment
    public void comment(Map<String,Object> params){

        Comment obj = objectMapper.convertValue(params.get("comment"),Comment.class);
        if(!obj.getIs_show())return; // 没审核不加action
        //Comment reply_obj;
        Action action = new Action();
        Long biao_id_receiver;
        //String replyer_info; // 回复哪个 主评论info还是子评论info 取决于是否回复子评论

        //  1.子评论下回复子评论，只发给replay_to_user_id
        if(!obj.getReplay_to_user_id().equals(0L)) {
            //reply_obj = commentMapper.selectById(obj.getReplay_to_comm_id()); // 回复的子评论对象
            biao_id_receiver = obj.getReplay_to_comm_id();
        }
        // 2. 不回复子评论 获取父评论
        else {
            //reply_obj = commentMapper.selectById(obj.getFather_comm_id());
            biao_id_receiver = obj.getFather_comm_id();
        }



        action.setUser_id(obj.getUser_id());
        //action.setUser(userMapper.selectById(obj.getUser_id()));
        Long receiver_id = obj.getReplay_to_user_id().equals(0L)?commentMapper.selectById(obj.getFather_comm_id()).getUser_id():obj.getReplay_to_user_id();
        action.setReceiver_id(receiver_id);
        action.setType("comment");
        action.setBiao("t_comment");
        action.setBiao_id_user(obj.getId());
        action.setBiao_id_receiver(biao_id_receiver);
        //action.setBiao_id(obj.getId());
        //Map<String, Object> obj2 = objectMapper.convertValue(obj,new TypeReference<Map<String, Object>>() {});
        //obj2.put("reply_info",reply_obj.getInfo());
        //action.setBiao_obj(obj2);

        if(!Db.lambdaQuery(Action.class)
                .eq(Action::getUser_id,obj.getUser_id())
                .eq(Action::getReceiver_id,obj.getUser_id())
                .eq(Action::getType,"comment")
                .eq(Action::getBiao_id,obj.getId())
                .exists()){
            actionMapper.insert(action);
            Db.lambdaUpdate(Action.class) // 因为审核通过时间不是评论的时间 更新
                    .eq(Action::getId,action.getId())
                    .set(Action::getCreate_time,obj.getCreate_time())
                    .update();
            // 发邮件
            params.put("receiver_id",receiver_id);
            send_email(params);
        }


    }

    // 文章直接评论(Father_comm_id=0L) product_comment  t_product
    public void comment2(Map<String,Object> params){
        Comment obj = objectMapper.convertValue(params.get("comment"),Comment.class);
        if(!obj.getIs_show())return; // 没审核不加action

        Action action = new Action();
        Product product = productMapper.selectById(obj.getProduct_id());
        Long biao_id_receiver = obj.getProduct_id();

        action.setUser_id(obj.getUser_id());
        action.setReceiver_id(product.getUser_id()); // 文章作者
        action.setType("product_comment");
        action.setBiao("t_comment");
        action.setBiao_id_user(obj.getId());
        action.setBiao_id_receiver(biao_id_receiver);
        //action.setBiao_id(obj.getId());
        //Map<String, Object> obj2 = objectMapper.convertValue(obj,new TypeReference<Map<String, Object>>() {});
        //obj2.put("reply_info",reply_obj.getInfo());
        //action.setBiao_obj(obj2);
        if(!Db.lambdaQuery(Action.class)
                .eq(Action::getUser_id,obj.getUser_id())
                .eq(Action::getReceiver_id,obj.getUser_id())
                .eq(Action::getType,"comment")
                .eq(Action::getBiao_id,obj.getId())
                .exists()){
            actionMapper.insert(action);
            Db.lambdaUpdate(Action.class) // 因为审核通过时间不是评论的时间 更新
                    .eq(Action::getId,action.getId())
                    .set(Action::getCreate_time,obj.getCreate_time())
                    .update();
        }

    }

    // 点赞文章 product_zan t_product
    public void zan_product(Map<String,Object> params, HttpSession session) {
        Product obj = objectMapper.convertValue(params.get("product"), Product.class);
        Action action = new Action();
        Long user_id = Tool.getUserSessionId(session);

        action.setUser_id(user_id);
        action.setReceiver_id(obj.getUser_id());
        action.setType("product_zan");
        action.setBiao("t_product");
        action.setBiao_id_user(obj.getId());
        action.setBiao_id_receiver(obj.getId());
        if(!Db.lambdaQuery(Action.class)
                .eq(Action::getUser_id,user_id)
                .eq(Action::getReceiver_id,obj.getUser_id())
                .eq(Action::getType,"product_zan")
                .eq(Action::getBiao_id_user,obj.getId())
                .exists()){
            actionMapper.insert(action);
        }
    }

    /**
     * 发邮件提醒option为提醒的 回复评论
     * @param
     */
    private void send_email(Map<String,Object> params) {
        Comment comment_replay_to = objectMapper.convertValue(params.get("comment_replay_to"),Comment.class);
        Comment comment = objectMapper.convertValue(params.get("comment"),Comment.class);
        UserOption userOption = Db.lambdaQuery(UserOption.class).eq(UserOption::getUser_id,comment_replay_to.getUser_id()).one();
        log.info("comment_replay_to:"+comment_replay_to);
        log.info("comment:"+comment);
        if(ObjectUtils.isEmpty(userOption))return;
        if(userOption.getSubscribe_comment_reply_by_mail())
            send_email2(params);
    }

    /**
     * 具体操作
     * @param params 1
     */
    private void send_email2(Map<String,Object> params) {
        Comment comment = objectMapper.convertValue(params.get("comment"),Comment.class);
        Comment comment_replay_to = objectMapper.convertValue(params.get("comment_replay_to"),Comment.class);
        User receiver = userMapper.selectById(comment_replay_to.getUser_id());
        User sender = userMapper.selectById(comment.getUser_id());

        if (!Tool.isValidEmail(receiver.getEmail())) log.info("不是有效邮箱");

        try {
            String content ="<tbody>" +
                    "<tr>" +
                    "<td>" +
                    "<div style=\"background:#fff\">" +
                    "<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">" +
                    "<thead>" +
                    "<tr>" +
                    "<td valign=\"middle\" style=\"padding-left:30px;background-color:#415A94;color:#fff;padding:20px 40px;font-size: 21px;\">" +
                    "西巴BLOG" +
                    "</td>" +
                    "</tr>" +
                    "</thead>" +
                    "<tbody>" +
                    "<tr style=\"padding:40px 40px 0 40px;display:table-cell\">" +
                    "<td style=\"font-size:24px;line-height:1.5;color:#000;margin-top:40px\">评论回复</td>" +
                    "</tr>" +
                    "<tr>" +
                    "<td style=\"font-size:14px;color:#333;padding:24px 40px 0 40px\">" +
                    "你的评论 \"" + comment_replay_to.getInfo() + "\" 收到回复：" +
                    "<br/><br/>" +
                    "<div style=\"border-left: 3px solid gainsboro;\">" +
                    "<span style=\"margin: 0 3px;margin-left: 10px;color:cornflowerblue;cursor: pointer;\">" +
                    "@ " + (sender.getWechat_nickname() == null ? sender.getName() : sender.getWechat_nickname()) + ":</span>" +
                    "<span style=\"color: #555;font-size: 0.9rem;\">" + comment.getInfo() + "</span>" +
                    "</div>" +
                    "</td>" +
                    "</tr>" +
                    "<tr style=\"padding:40px;display:table-cell\">" +
                    "</tr>" +
                    "</tbody>" +
                    "</table>" +
                    "</div>" +
                    "<div>" +
                    "<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">" +
                    "<tbody>" +
                    "<tr>" +
                    "<td style=\"padding:20px 40px;font-size:12px;color:#999;line-height:20px;background:#f7f7f7\">" +
                    "<a href=\"https://www.yjztest.top/new_blog/\" style=\"font-size:14px;color:#929292\">前往BLOG</a>" +
                    "</td>" +
                    "</tr>" +
                    "</tbody>" +
                    "</table>" +
                    "</div>" +
                    "</td>" +
                    "</tr>" +
                    "</tbody>";

            log.info("邮件发送成功"+receiver.getEmail());
            Tool.sendEmail(receiver.getEmail(), "西巴BLOG-收到评论回复:"+comment.getInfo(), content);
        }catch (Exception e){
            log.error("邮件发送失败"+e.toString());
        }


    }

}
