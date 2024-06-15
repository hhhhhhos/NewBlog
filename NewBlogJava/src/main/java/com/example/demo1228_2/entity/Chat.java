package com.example.demo1228_2.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author yjz
 * @since 2024-05-20
 */
@Data
@TableName("t_chat")
public class Chat implements Serializable {

    /**
     * 聊天表
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long user_id;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long receiver_id;

    private String info;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime create_time;

    Boolean is_read;

    private String group_uuid;

}
