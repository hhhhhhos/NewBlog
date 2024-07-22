package com.example.demo1228_2.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Data;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data //自动生成get set方法
@JsonIgnoreProperties(ignoreUnknown = true) // json遇到实体没有的属性 不映射
@TableName(value = "t_product", autoResultMap = true) //实体绑定mysql的t_user表（用于mapper的mybatisplus）
public class Product {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    Long id;
    String name;
    BigDecimal price;
    int num;
    String info;

    String content;
    String photo;

    String photo_url;

    String photo_shot;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime create_time;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime update_time;

    String type;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    Long user_id;

    int visited_num;

    int sold_num;

    BigDecimal rate;

    int rate_num;

    //@JsonFormat(shape = JsonFormat.Shape.STRING)
    //Long product_related_list_id;

    String type2;

    private Integer version;

    @TableField(typeHandler = JacksonTypeHandler.class)
    List<String> photo_list;

    @TableField(typeHandler = JacksonTypeHandler.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING) // Long异常的处理
    private List<Long> love_list;

    String visible;
}
