package com.example.demo1228_2.service.impl;

import com.example.demo1228_2.entity.Chat;
import com.example.demo1228_2.mapper.ChatMapper;
import com.example.demo1228_2.service.IChatService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author yjz
 * @since 2024-05-20
 */
@Service
public class ChatServiceImpl extends ServiceImpl<ChatMapper, Chat> implements IChatService {

}
