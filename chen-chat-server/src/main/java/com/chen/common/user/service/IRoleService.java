package com.chen.common.user.service;

import com.chen.common.user.domain.enums.RoleEnum;

/**
 * <p>
 * 角色表 服务类
 * </p>
 *
 * @author Chen
 * @since 2023-06-04
 */
public interface IRoleService {

    /**
     * 是否有某个权限，临时做法
     *
     * @return
     */
    boolean hasPower(Long uid, RoleEnum roleEnum);

}
