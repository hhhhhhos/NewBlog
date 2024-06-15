package com.example.demo1228_2.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo1228_2.Vo.ChatHistoryItemVo;
import com.example.demo1228_2.dto.CommentOrderUserRateDto;
import com.example.demo1228_2.entity.Chat;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author yjz
 * @since 2024-05-20
 */
public interface ChatMapper extends BaseMapper<Chat> {

    @Select("SELECT DISTINCT group_uuid FROM t_chat " +
            "WHERE (user_id = #{user_id} AND receiver_id = #{receiver_id}) " +
            "OR (user_id = #{receiver_id} AND receiver_id = #{user_id})")
    String selectGroupId(Long user_id, Long receiver_id);

    @Select("SELECT group_uuid, MAX(create_time) AS latest_create_time, " +
            "SUM(CASE WHEN is_read = 0 THEN 1 ELSE 0 END) AS unread " +
            "FROM t_chat " +
            "WHERE user_id = #{user_id} OR receiver_id = #{user_id} " +
            "GROUP BY group_uuid " +
            "ORDER BY latest_create_time DESC ")
    Page<Map<String,Object>> selectGroupIds(Page page, Long user_id);

    @Select("SELECT * FROM t_chat " +
            "WHERE (user_id = #{user_id} AND receiver_id = #{receiver_id}) " +
            "OR (user_id = #{receiver_id} AND receiver_id = #{user_id}) " +
            "ORDER BY create_time DESC ")
    Page<Chat> selectChats(Page page, Long user_id, Long receiver_id);

    @Update("UPDATE t_chat SET is_read = 1 " +
            "WHERE (user_id = #{user_id} AND receiver_id = #{receiver_id}) " +
            "OR (user_id = #{receiver_id} AND receiver_id = #{user_id})")
    void updateUnreadStatus(Long user_id, Long receiver_id);

}
