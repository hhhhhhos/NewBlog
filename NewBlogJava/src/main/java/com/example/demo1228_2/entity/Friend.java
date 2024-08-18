package com.example.demo1228_2.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author yjz
 * @since 2024-08-15
 */
@Data
@TableName("t_friend")
public class Friend implements Serializable {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long user_id;

    private String name;

    private String info;

    private String qq_mail;

    /**
     * 友链链接
     */
    private String blog_url;

    /**
     * 是否是粉丝（否则是我关注的）
     */
    private Boolean fan;

    private Boolean invalid;

    private String avata_url;

}
