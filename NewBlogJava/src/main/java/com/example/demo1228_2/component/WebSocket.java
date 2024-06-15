package com.example.demo1228_2.component;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.ServerEndpointConfig;

import com.example.demo1228_2.Vo.ChatHistoryItemVo;
import com.example.demo1228_2.config.Tool;
import com.example.demo1228_2.entity.User;
import com.example.demo1228_2.mapper.UserMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentHashMap;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@ServerEndpoint("/websocket/{userId}") // 接口路径 ws://localhost:8087/webSocket/userId;
//@ServerEndpoint(value = "/websocket", configurator = HttpSessionConfigurator.class) // 接口路径 ws://localhost:8087/webSocket/userId;
public class WebSocket {

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;
    /**
     * 用户ID
     */
    private String userId;

    private HttpSession httpSession;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    UserMapper userMapper;

    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    //虽然@Component默认是单例模式的，但springboot还是会为每个websocket连接初始化一个bean，所以可以用一个静态set保存起来。
    //  注：底下WebSocket是当前类名
    private static CopyOnWriteArraySet<WebSocket> webSockets =new CopyOnWriteArraySet<>();
    // 用来存在线连接用户信息
    public static ConcurrentHashMap<String,Session> sessionPool = new ConcurrentHashMap<String,Session>();

    /**
     * 链接成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session,EndpointConfig config,@PathParam(value="userId")String userId) {
        try {
            //httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
            this.session = session;
            this.userId = userId;
            webSockets.add(this);
            log.info("this.userId"+this.userId+"session"+session);
            sessionPool.put(userId, session);
            log.info("【websocket消息】有新的连接，总数为:"+webSockets.size());
            log.info("【websocket消息】userId:"+userId);
        } catch (Exception e) {
            log.info("【websocket异常】"+e.getMessage());
        }
    }


    /**
     * 链接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        try {
            webSockets.remove(this);
            sessionPool.remove(this.userId);
            log.info("【websocket消息】连接断开，总数为:"+webSockets.size());
        } catch (Exception e) {
        }
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message
     */
    @OnMessage
    public void onMessage(String message) {
        log.info("【websocket消息】收到客户端消息:"+message);
    }

    /** 发送错误时的处理
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {

        log.error("用户错误,原因:"+error.getMessage());
        error.printStackTrace();
    }


    // 此为广播消息
    public void sendAllMessage(String message) {
        log.info("【websocket消息】广播消息:"+message);
        for(WebSocket webSocket : webSockets) {
            try {
                if(webSocket.session.isOpen()) {
                    webSocket.session.getAsyncRemote().sendText(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 此为单点消息
    public void sendOneMessage(Map<String,Object> params) {
        String receiver_id = params.get("receiver_id").toString();
        ChatHistoryItemVo chatHistoryItemVo = objectMapper.convertValue(params.get("chatHistoryItemVo"), ChatHistoryItemVo.class);

        Session session = sessionPool.get(receiver_id);
        if (session != null&&session.isOpen()) {
            try {
                log.info("【websocket消息】To"+receiver_id+"单点消息:"+chatHistoryItemVo.getLast_info());
                session.getAsyncRemote().sendText(objectMapper.writeValueAsString(params));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 此为单点消息(多人)
    public void sendMoreMessage(String[] userIds, String message) {
        for(String userId:userIds) {
            Session session = sessionPool.get(userId);
            if (session != null&&session.isOpen()) {
                try {
                    log.info("【websocket消息】 单点消息:"+message);
                    session.getAsyncRemote().sendText(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }



}

