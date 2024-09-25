package com.example.demo1228_2.mapper;

import com.example.demo1228_2.entity.Tag;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author yjz
 * @since 2024-09-06
 */
public interface TagMapper extends BaseMapper<Tag> {

    @Select("select DISTINCT tag_int from t_tag where product_id = #{Id}")
    List<Integer> selectUniqueTagIntByProductId(Long Id);


}
