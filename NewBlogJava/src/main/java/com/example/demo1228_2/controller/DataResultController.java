package com.example.demo1228_2.controller;


import com.example.demo1228_2.entity.DataResult;
import com.example.demo1228_2.mapper.DataResultMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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

    @PostMapping("/updatebyadmin")
    public int all(@RequestBody DataResult dataResult){
        return dataResultMapper.updateById(dataResult);
    }


}
