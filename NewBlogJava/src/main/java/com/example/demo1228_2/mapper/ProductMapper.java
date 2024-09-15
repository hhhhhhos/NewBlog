package com.example.demo1228_2.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo1228_2.entity.Product;
import com.example.demo1228_2.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    // 归档
    @Select("SELECT id,name,create_time,update_time,type,is_top,is_show,is_on_homepage FROM t_product ORDER BY create_time DESC")
    List<Map<String,Object>> selectIdNameTime();

    // 归档 带 type筛选
    @Select("SELECT id,name,create_time,update_time,type,is_top,is_show,is_on_homepage FROM t_product WHERE type = #{type} ORDER BY create_time DESC")
    List<Map<String,Object>> selectIdNameTime2(int type);

    List<Map<String, Object>> selectIdNameTime3(int type,String is);

    List<Map<String,Object>> getTagsGroupedByProductIds(List<Long> productIds);

}
