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
 *  用户个人设置
 * </p>
 *
 * @author yjz
 * @since 2024-08-17
 */
@Data
@TableName("t_user_option")
public class UserOption implements Serializable {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long user_id;

    private Boolean show_product_scale_win;

    private Boolean subscribe_comment_reply_by_mail;

    public UserOption(){

    }

    public UserOption(Long user_id){
        this.user_id = user_id;
    }

}
