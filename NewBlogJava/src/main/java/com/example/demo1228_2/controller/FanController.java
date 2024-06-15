package com.example.demo1228_2.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.example.demo1228_2.config.CustomException;
import com.example.demo1228_2.config.R;
import com.example.demo1228_2.config.Tool;
import com.example.demo1228_2.entity.Fan;
import com.example.demo1228_2.entity.User;
import com.example.demo1228_2.mapper.FanMapper;
import com.example.demo1228_2.mapper.UserMapper;
import com.example.demo1228_2.service.impl.FanServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author yjz
 * @since 2024-05-14
 */
@RestController
@RequestMapping("/fan")
public class FanController {

    @Autowired
    FanServiceImpl fanService;

    @Autowired
    FanMapper fanMapper;

    @Autowired
    UserMapper userMapper;

    /**
     * 查询user_id的粉丝数 关注数 是否已粉
     * @param user_id 1
     * @return 1
     */
    @GetMapping("/info")
    public R<Boolean> get(@RequestParam String user_id,HttpSession session){
        // 关注人数
        String gz = Db.lambdaQuery(Fan.class)
                .eq(Fan::getUser_id,user_id).count().toString();
        // 被关注人数
        String fs = Db.lambdaQuery(Fan.class)
                .eq(Fan::getUser_id2,user_id).count().toString();
        // 是否已关注
        Boolean is_Fan = Db.lambdaQuery(Fan.class).eq(Fan::getUser_id,Tool.getUserSessionId(session))
                .eq(Fan::getUser_id2,user_id).exists();
        return R.success(is_Fan).add("gz",gz).add("fs",fs);
    }

    /**
     * 返回粉丝或者关注列表page
     * @param params 1
     * @param session 1
     * @return 1
     */
    @GetMapping("/page")
    public R<Page> get(@RequestParam Map<String,String> params, HttpSession session){
        Long user_id;
        int currentPage;
        int PageSize;
        String p1;
        try {
            user_id = Long.parseLong(params.get("user_id"));
            currentPage = Integer.parseInt(params.get("currentPage"));
            PageSize = Integer.parseInt(params.get("PageSize"));
            p1 = params.get("p1");
        }catch (Exception e){
            return R.error(e.getMessage());
        }
        Page page = new Page<>(currentPage, PageSize);

        try {
            // 粉丝列表
            if (p1.equals("fs")) {
                page = Db.lambdaQuery(Fan.class).eq(Fan::getUser_id2,user_id).page(page);

                List<Fan> list = page.getRecords();

                List<User> newlist = list.stream().map(fan -> userMapper.selectById(fan.getUser_id())).toList();

                page.setRecords(newlist);

            // 关注列表
            } else if (p1.equals("gz")) {
                page = Db.lambdaQuery(Fan.class).eq(Fan::getUser_id,user_id).page(page);

                List<Fan> list = page.getRecords();

                List<User> newlist = list.stream().map(fan -> userMapper.selectById(fan.getUser_id2())).toList();

                page.setRecords(newlist);

            } else {
                throw new CustomException("参数错");
            }
        }catch (Exception e){
            return R.error(e.getMessage());
        }

        return R.success(page);
    }


    /**
     * 关注或取关
     * @param user_id 1
     * @param session 1
     * @return 1
     */
    @PostMapping("/addordelete")
    public R<String> add(@RequestParam String user_id, HttpSession session){
        if(Tool.getUserSessionId(session).equals(0L))return R.error("未登录");
        try {
            if (user_id.equals(Tool.getUserSessionId(session).toString()))
                throw new CustomException("不能粉自己");
            // 查询是否已粉
            Fan fan = Db.lambdaQuery(Fan.class)
                    .eq(Fan::getUser_id,Tool.getUserSessionId(session))
                    .eq(Fan::getUser_id2,user_id).one();
            // 未粉关注
            if(fan==null){
                fan = new Fan();
                fan.setUser_id(Tool.getUserSessionId(session));
                fan.setUser_id2(Long.parseLong(user_id));
                if(fanMapper.insert(fan)!=1)
                    throw new CustomException("关注失败");
                else
                    return R.success("关注成功");
            // 已粉取关
            }else{
                if(fanMapper.deleteById(fan)!=1)
                    throw new CustomException("取关失败");
                else
                    return R.success("取关成功");
            }
        }catch (Exception e){
            return R.error(e.getMessage());
        }

    }

}
