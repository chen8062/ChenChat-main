package com.chen.common.user.dao;

import com.chen.common.user.domain.entity.Black;
import com.chen.common.user.mapper.BlackMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 黑名单 服务实现类
 * </p>
 *
 * @author Chen
 * @since 2023-05-21
 */
@Service
public class BlackDao extends ServiceImpl<BlackMapper, Black> {

}
