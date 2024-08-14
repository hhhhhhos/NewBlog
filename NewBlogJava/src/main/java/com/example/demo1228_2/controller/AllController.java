package com.example.demo1228_2.controller;

import com.example.demo1228_2.config.R;
import com.example.demo1228_2.mapper.AllMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/all")
@Slf4j // 自动生成log对象
public class AllController {

    @Autowired
    AllMapper allMapper;

    /**
     * 获取某个表的某元素统计个数 并逆序排序
     * @param yuansu 1
     * @param biao 1
     * @return list
     */
    @GetMapping("/paiming/{yuansu}/{biao}")
    public List get(@PathVariable String yuansu,
                    @PathVariable String biao) {
        return allMapper.getYuanSuPaiMing(yuansu,biao);
    }
}
