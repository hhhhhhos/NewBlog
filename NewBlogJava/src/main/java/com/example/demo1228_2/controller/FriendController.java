package com.example.demo1228_2.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.example.demo1228_2.config.CustomException;
import com.example.demo1228_2.config.R;
import com.example.demo1228_2.config.Tool;
import com.example.demo1228_2.entity.Friend;
import com.example.demo1228_2.mapper.FriendMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author yjz
 * @since 2024-08-15
 */
@RestController
@RequestMapping("/friend")
public class FriendController {

    @GetMapping("/page")
    public R get(@RequestParam Map<String,String> params, HttpSession session) {
        int currentPage;
        int PageSize;
        try {

            currentPage = Integer.parseInt(params.get("currentPage"));
            PageSize = Integer.parseInt(params.get("PageSize"));
            Boolean fan = Boolean.parseBoolean(params.get("fan"));
            return R.success(Db.lambdaQuery(Friend.class)
                    .eq(Friend::getFan, fan).eq(Friend::getInvalid,false)
                    .page(new Page<>(currentPage, PageSize)));

        }catch (Exception e){
            return R.error(e.getMessage());
        }


    }

    @Autowired
    FriendMapper friendMapper;

    @PostMapping("/addone")
    public R post(@RequestBody Friend friend,HttpSession session){
        try{
            if(!friend.getFan()){
                if(!Tool.IsUserAdmin(session))
                    throw new CustomException("不是管理员");
                else
                    friend.setInvalid(false);
            }

            friend.setUser_id(Tool.getUserSessionId(session));
            if(friendMapper.insert(friend)!=1)
                return R.error("数据库插入失败");
            else
                return R.success("将在审核后展示");
        }catch (Exception e){
            return R.error(e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public R delete(@RequestBody Friend friend,HttpSession session){
        try{
            if(friend.getUser_id()!=null&&!friend.getUser_id().equals(Tool.getUserSessionId(session))&&!Tool.IsUserAdmin(session))
                throw new CustomException("非本人且非管理员");

            if(friend.getUser_id()==null&&!Tool.IsUserAdmin(session))
                throw new CustomException("非管理员");

            if(friend.getUser_id()!=null&&Tool.getUserSessionId(session).equals(1766859847220883457L))
                throw new CustomException("visitor无权限");

            if(friendMapper.deleteById(friend.getId())==1)
                return R.success("删除成功");
            else
                return R.error("删除失败，数据库返回0");
        }catch (Exception e){
            return R.error(e.getMessage());
        }
    }

}
