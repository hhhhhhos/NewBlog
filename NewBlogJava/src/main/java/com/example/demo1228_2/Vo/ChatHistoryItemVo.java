package com.example.demo1228_2.Vo;

import com.example.demo1228_2.entity.User;
import lombok.Data;

/**
 * /user/chat/history的Vo
 */
@Data
public class ChatHistoryItemVo {

    String group_uuid;

    String last_info;

    String last_sender_id;

    String latest_create_time;

    User receiver;

    int unread;


}
