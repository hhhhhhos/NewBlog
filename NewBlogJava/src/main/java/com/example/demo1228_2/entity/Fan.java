package com.example.demo1228_2.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * <p>
 *
 * </p>
 *
 * @author yjz
 * @since 2024-05-14
 */
@Data
@TableName("t_fan")
public class Fan implements Serializable {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * 用户id
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long user_id;

    /**
     * 被粉用户 id
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long user_id2;


}
