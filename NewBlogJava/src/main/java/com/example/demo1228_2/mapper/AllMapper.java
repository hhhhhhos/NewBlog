package com.example.demo1228_2.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo1228_2.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface AllMapper extends BaseMapper<Product> {

    // 获取某元素 统计
    // 表名只能用${ ？ 不然会解析成 ’yaunsu‘ ？
    @Select("SELECT ${yaunsu} as yuansu, COUNT(*) as count  "+
            "FROM ${biao} " +
            "GROUP BY ${yaunsu} " +
            "ORDER BY COUNT(*) DESC")
    List<Map<String,Object>> getYuanSuPaiMing(String yaunsu,String biao);
}
