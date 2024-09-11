package com.example.demo1228_2.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo1228_2.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface AllMapper extends BaseMapper {

    // 获取某元素 统计
    // 表名只能用${ ？ 不然会解析成 ’yaunsu‘ ？
    @Select("SELECT ${yaunsu} as yuansu, COUNT(*) as count  "+
            "FROM ${biao} " +
            "GROUP BY ${yaunsu} " +
            "ORDER BY COUNT(*) DESC")
    List<Map<String,Object>> getYuanSuPaiMing(String yaunsu,String biao);

    // 泛类型筛选 根据biao不同
    Page<Map<String, Object>> selectpagebyadminWithSearch_SetList(Page<Map> page, Map<String,List<Map>> yaunsuListMap, String biao, String value2, String name, LocalDateTime startDate,LocalDateTime endDate,Long id,List<Integer> tagList);

    List<Map<String, Object>> selectGroupedData(String yaunsu,Map<String,List<Map<String,Object>>> yaunsuListMap, String biao,String value2, String name, LocalDateTime startDate,LocalDateTime endDate,String search_text,Long id);

    int updateonebyadmin(Map<String, Object> obj,String biao);

    int deleteTByIds(List<Long> Ids,String biao);

    int insertonebyadmin(Map<String, Object> obj,String biao);

    @Select("select tag_int as yuansu, COUNT(*) as count from t_tag group by yuansu")
    List<Map<String,Object>> selectcountTTagLabel();
}
