package com.example.demo1228_2.controller;


import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.example.demo1228_2.config.CustomException;
import com.example.demo1228_2.config.R;
import com.example.demo1228_2.config.Tool;
import com.example.demo1228_2.entity.UserOption;
import com.example.demo1228_2.mapper.UserOptionMapper;
import org.apache.ibatis.annotations.Update;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author yjz
 * @since 2024-08-17
 */
@RestController
@RequestMapping("/user-option")
public class UserOptionController {

    @Autowired
    UserOptionMapper userOptionMapper;

    @GetMapping("/get")
    public R get(@RequestParam Long id, HttpSession session){
        try{
            UserOption userOption = Db.lambdaQuery(UserOption.class).eq(UserOption::getUser_id,id).one();
            // 用户设置不存在 第一次初始化默认设置
            if(userOption==null){
                //（设置user_id）
                userOption = new UserOption(Tool.getUserSessionId(session));
                userOptionMapper.insert(userOption);
                // 手动回显
                userOption = Db.lambdaQuery(UserOption.class).eq(UserOption::getUser_id,id).one();
            }

            return R.success(userOption);
        }catch (Exception e){
            return R.error(e.getMessage());
        }
    }

    @PutMapping("/update")
    public R update(@RequestBody UserOption userOption,HttpSession session){
        try {
            if (!userOption.getUser_id().equals(Tool.getUserSessionId(session)))
                throw new CustomException("不能改其他人设置");

            if(userOptionMapper.updateById(userOption)!=1)
                throw new CustomException("修改失败，数据库返回0");
            else
                return R.success("");

        }catch (Exception e){
            return R.error(e.getMessage());
        }

    }

}
