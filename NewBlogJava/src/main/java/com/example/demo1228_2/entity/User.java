package com.example.demo1228_2.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.example.demo1228_2.Vo.Address;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data //自动生成get set方法
@TableName(value = "t_user", autoResultMap = true) //实体绑定mysql的t_user表（用于mapper的mybatisplus）
public class User {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;
    private String name;
    private String age;
    private String sex;
    private String phone;
    private String email;



    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime create_time;
    private String password;
    private String role;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<Address> addresses;

    private BigDecimal money;

    //@Version
    private Integer version; // 乐观锁版本号

    String wechat_unionid;

    String wechat_nickname;

    String wechat_headimgurl;

    public User(String name,String password){
        this.name = name;
        this.password = password;
    }
    public User(String name,String email,String password){
        this.name = name;
        this.email = email;
        this.password = password;
    }

    String ip_location;

    String blog;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    LocalDateTime last_visited_time;
    public User(){}
}
