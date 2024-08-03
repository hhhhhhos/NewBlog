package com.example.demo1228_2.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo1228_2.entity.Product;
import com.example.demo1228_2.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    // 归档
    @Select("SELECT id,name,create_time,update_time,type FROM t_product ORDER BY create_time DESC")
    List<Map<String,Object>> selectIdNameTime();

}
