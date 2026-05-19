package com.chen.common.chat.dao;

import com.chen.common.chat.domain.entity.WxMsg;
import com.chen.common.chat.mapper.WxMsgMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 微信消息表 服务实现类
 * </p>
 *
 * @author Chen
 * @since 2023-05-16
 */
@Service
public class WxMsgDao extends ServiceImpl<WxMsgMapper, WxMsg> {

}
