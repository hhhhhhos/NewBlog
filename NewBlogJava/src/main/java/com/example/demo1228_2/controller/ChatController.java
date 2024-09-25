package com.example.demo1228_2.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.example.demo1228_2.Vo.ChatHistoryItemVo;
import com.example.demo1228_2.component.WebSocket;
import com.example.demo1228_2.config.CustomException;
import com.example.demo1228_2.config.R;
import com.example.demo1228_2.config.Tool;
import com.example.demo1228_2.entity.Action;
import com.example.demo1228_2.entity.Chat;
import com.example.demo1228_2.entity.KefuChatHistory;
import com.example.demo1228_2.entity.User;
import com.example.demo1228_2.mapper.ChatMapper;
import com.example.demo1228_2.mapper.UserMapper;
import com.example.demo1228_2.service.impl.ChatServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author yjz
 * @since 2024-05-20
 */
@Slf4j
@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    ChatServiceImpl chatService;
    @Autowired
    ChatMapper chatMapper;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    WebSocket webSocket;

    /**
     * 发送一条聊天到数据库
     * @param params 1
     * @param session 1
     * @return 1
     */
    @PostMapping("/add")
    public R add(@RequestBody Map<String,Object> params, HttpSession session){
        try{
            String info = params.get("info").toString();
            String photo_url = ObjectUtils.isEmpty(params.get("photo_url"))?null:params.get("photo_url").toString();
            Long receiver_id = Long.parseLong(params.get("receiver_id").toString());
            Long user_id = Tool.getUserSessionId(session);
            if(user_id.equals(receiver_id))throw new CustomException("不能给自己发消息");
            if(info.length()>200)throw new CustomException("长度过长");
            Chat chat = new Chat();
            chat.setInfo(info);
            chat.setPhoto_url(photo_url);
            chat.setUser_id(user_id);
            chat.setReceiver_id(receiver_id);
            // 查有无相符合组别ID
            String group_uuid = chatMapper.selectGroupId(user_id,receiver_id);
            // 没就新建 有就设置
            if(group_uuid==null) {group_uuid = UUID.randomUUID().toString();chat.setGroup_uuid(group_uuid);}
            else chat.setGroup_uuid(group_uuid);

            if(chatService.save(chat)) {
                // 判断是否在线 在线就发送
                if(WebSocket.sessionPool.get(receiver_id.toString())!=null) {
                    // 发websocket给收消息的人（如果在线） (构造前端要的Vo信息)
                    ChatHistoryItemVo chatHistoryItemVo = new ChatHistoryItemVo();
                    chatHistoryItemVo.setGroup_uuid(group_uuid);
                    chatHistoryItemVo.setLast_info(info);
                    chatHistoryItemVo.setLast_sender_id(user_id.toString());
                    chatHistoryItemVo.setLatest_create_time(Tool.getDateTime());
                    chatHistoryItemVo.setReceiver(userMapper.selectById(user_id));
                    chatHistoryItemVo.setUnread(Integer.parseInt(Db.lambdaQuery(Chat.class)
                            .eq(Chat::getGroup_uuid, group_uuid)
                            .eq(Chat::getIs_read, false)
                            .count()
                            .toString()));
                    params.put("chatHistoryItemVo", chatHistoryItemVo);
                    params.put("chat",chat);
                    webSocket.sendOneMessage(params);
                }
                return R.success("success");
            }
            else
                return R.error("发送数据库失败");
        }catch (Exception e){
            return R.error(e.getMessage());
        }
    }

    /**
     * 设置chat为已读
     * @param chat 1
     */
    @PostMapping("/setIsRead")
    public void add(@RequestBody Chat chat) {
        //log.info("chat:"+chat);
        chat.setIs_read(true);
        chatMapper.updateById(chat);
    }

    /**
     * 返回未读消息总数
     * @param session 1
     * @return 1
     */
    @GetMapping("/getUnreadNum")
    public R testt234(HttpSession session){
        int res = Integer.parseInt(Db.lambdaQuery(Chat.class)
                .eq(Chat::getReceiver_id,Tool.getUserSessionId(session))
                .eq(Chat::getIs_read,false)
                .count()
                .toString())
                +
                Integer.parseInt(Db.lambdaQuery(Action.class)
                        .eq(Action::getReceiver_id,Tool.getUserSessionId(session))
                        .eq(Action::getIs_read,false)
                        .count()
                        .toString());


        return R.success(Integer.toString(res));
    }

    /**
     * 聊天记录分页(和谁聊过 返回分组)
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
        page = chatMapper.selectGroupIds(page,user_id);
        page.setRecords(page.getRecords().stream().map(item->{
            Map<String,Object> map = objectMapper.convertValue(item, Map.class);
            //map.put("latest_create_time",map.get("latest_create_time").toString().replaceFirst("(\\+\\d{2}):\\d{2}$", "+08:00"));
            String group_uuid = map.get("group_uuid").toString();
            Page<Chat> page1 = new Page<>(1,1);
            Chat chat = Db.lambdaQuery(Chat.class).eq(Chat::getGroup_uuid,group_uuid).orderByDesc(Chat::getCreate_time)
                    .page(page1).getRecords().get(0);
            Long receiver_id;
            if(!chat.getUser_id().equals(user_id))
                receiver_id = chat.getUser_id();
            else
                receiver_id = chat.getReceiver_id();
            try {
                map.put("receiver", userMapper.selectById(receiver_id));
            }catch (Exception e){
                log.info(e.getMessage());
            }
            map.put("last_info",chat.getInfo());
            map.put("last_sender_id",chat.getUser_id().toString());
            return map;
        }).toList());

        return R.success(page);

    }

    /**
     * 和某个单人聊天记录分页
     * @param params 1
     * @param session 1
     * @return 1
     */
    @GetMapping("/pagewithone")
    public R testt2340(@RequestParam Map<String,String> params,HttpSession session) {
        Long user_id;
        Long receiver_id;
        int currentPage;
        int PageSize;
        try {
            user_id = Tool.getUserSessionId(session);
            receiver_id = Long.parseLong(params.get("receiver_id"));
            currentPage = Integer.parseInt(params.get("currentPage"));
            PageSize = Integer.parseInt(params.get("PageSize"));
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
        Page<Chat> page = new Page<>(currentPage, PageSize);
        page = chatMapper.selectChats(page,user_id,receiver_id);
        // 已读(如果接收人是本人)
        page.setRecords(page.getRecords().stream().map(chat-> {
            if(chat.getReceiver_id().equals(user_id))chat.setIs_read(true);
            return chat;
        }).collect(Collectors.toCollection(ArrayList::new)));
        // 设置 他发给我的 已读
        Db.lambdaUpdate(Chat.class)
                .set(Chat::getIs_read, 1)
                .and(wrapper -> wrapper
                        .eq(Chat::getUser_id, receiver_id)
                        .eq(Chat::getReceiver_id, user_id)
                        .or()
                )
                .update();

        Collections.reverse(page.getRecords());  // 倒置List
        chatService.updateBatchById(page.getRecords());

        return R.success(page).add("receiver",userMapper.selectById(receiver_id))
                .add("user",userMapper.selectById(user_id));
    }
}
