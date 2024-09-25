package com.example.demo1228_2.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Map;

import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
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
 * @since 2024-09-19
 */
@Data
@TableName(value = "t_action", autoResultMap = true)
public class Action implements Serializable {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 发起人
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long user_id;


    /**
     * 接受行为人
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long receiver_id;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime create_time;

    /**
     * 默认未读

     */
    private Boolean is_read;

    /**
     * 操作行为是什么
     */
    private String type;

    /**
     * 与之关联的表名
     */
    private String biao;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long biao_id;

    @TableField(typeHandler = JacksonTypeHandler.class)
    Map<String,Object> biao_obj;

    /**
     * user关于biao的biao主键
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long biao_id_user;

    /**
     * receiver关于biao的biao主键
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long biao_id_receiver;

}
