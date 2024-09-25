package com.example.demo1228_2.component;

import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.example.demo1228_2.entity.*;
import com.example.demo1228_2.mapper.DataResultMapper;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class ScheduledTasks {

    @Autowired
    DataResultMapper dataResultMapper;

    /*
    @Scheduled(cron = "0 20 17 * * ?")
    这个 cron 表达式的含义是：

    0 ：秒（表示每分钟的第 0 秒）
    20 ：分钟（表示第 20 分钟）
    17 ：小时（表示 24 小时制的 17 点，也就是下午 5 点）
    * ：日（表示每天）
    * ：月份（表示每个月）
    ? ：星期几（表示不指定）
     */
    // 每天凌晨1点执行(记录今日dashboard数据)
    @Scheduled(cron = "0 0 1 * * ?")
    public void cronTask() {
        log.info("cron任务(记录今日dashboard数据)已执行");
        DataResult dataResult = dataResultMapper.selectById(45698);
        Map<String,Object> params = new HashMap<>();

        params.put("home_visit_num", dataResult.getHome_visitors());
        params.put("dashboard_visit_num", dataResult.getBack_visitors());
        params.put("request_num", Db.lambdaQuery(UserAgentDetails.class).count());

        params.put("product_num",Db.lambdaQuery(Product.class).count());
        params.put("user_num",Db.lambdaQuery(User.class).count());
        params.put("comment_num",Db.lambdaQuery(Comment.class).count());
        params.put("chat_num",Db.lambdaQuery(Chat.class).count());
        params.put("kefu_num",Db.lambdaQuery(KefuChatHistory.class).count());

        // 今日数据记录
        Map<String,Object>other_stuff_map = dataResult.getOther_stuff_map();
        other_stuff_map.put("dashboard_params_"+ LocalDate.now(),params);
        dataResult.setOther_stuff_map(other_stuff_map);
        // 前天数据删除
        other_stuff_map.remove("dashboard_params_"+ LocalDate.now().minusDays(2));

        dataResultMapper.updateById(dataResult);
    }
}
