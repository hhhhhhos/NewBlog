package com.example.demo1228_2.mapper;

import com.example.demo1228_2.entity.DataResult;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author yjz
 * @since 2024-04-22
 */
public interface DataResultMapper extends BaseMapper<DataResult> {

    @Select("select type, count(*) as count from t_product group by type")
    List<Map<String,String>> type_count();
}
