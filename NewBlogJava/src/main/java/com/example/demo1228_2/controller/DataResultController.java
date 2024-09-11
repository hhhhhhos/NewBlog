package com.example.demo1228_2.controller;


import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.example.demo1228_2.entity.DataResult;
import com.example.demo1228_2.entity.Tag;
import com.example.demo1228_2.mapper.DataResultMapper;
import com.example.demo1228_2.mapper.TagMapper;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author yjz
 * @since 2024-04-22
 */
@Slf4j
@RestController
@RequestMapping("/data-result")
public class DataResultController {

    @Autowired
    DataResultMapper dataResultMapper;

    @GetMapping("/all")
    public DataResult all(){
        log.info(dataResultMapper.selectById(45698L).getEn_list().toString());
        return dataResultMapper.selectById(45698L);
    }

    @GetMapping("/type_count")
    public List cc(){
        return dataResultMapper.type_count();
    }

    @PostMapping("/updatebyadmin")
    public int all(@RequestBody DataResult dataResult){
        handleBiaoqian_delete(dataResult);
        return dataResultMapper.updateById(dataResult);
    }

    /**
     * 针对dataResult更新后 不存在的标签 从t_tag表删除
     * @param dataResult
     */
    private void handleBiaoqian_delete(DataResult dataResult) {
        DataResult dataResult_db = dataResultMapper.selectById(45698L);
        Map<String,String> biaoqian_map_db = dataResult_db.getBiaoqian_map();
        Map<String,String> biaoqian_map = dataResult.getBiaoqian_map();

        biaoqian_map_db.forEach((key, value) -> {
            // db有 你没有 你已经删了
            if(!biaoqian_map.containsKey(key)){
                Db.lambdaUpdate(Tag.class).eq(Tag::getTag_int,Integer.parseInt(key)).remove();
            }
        });




    }


}
