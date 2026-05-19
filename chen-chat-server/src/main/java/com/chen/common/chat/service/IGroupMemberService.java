package com.chen.common.chat.service;

import com.chen.common.chat.domain.vo.request.admin.AdminAddReq;
import com.chen.common.chat.domain.vo.request.admin.AdminRevokeReq;
import com.chen.common.chat.domain.vo.request.member.MemberExitReq;

/**
 * <p>
 * 群成员表 服务类
 * </p>
 *
 * @author Chen
 * @since 2023-07-16
 */
public interface IGroupMemberService {
    /**
     * 增加管理员
     *
     * @param uid     用户ID
     * @param request 请求信息
     */
    void addAdmin(Long uid, AdminAddReq request);

    /**
     * 撤销管理员
     *
     * @param uid     用户ID
     * @param request 请求信息
     */
    void revokeAdmin(Long uid, AdminRevokeReq request);

    /**
     * 退出群聊
     *
     * @param uid     用户ID
     * @param request 请求信息
     */
    void exitGroup(Long uid, MemberExitReq request);
}
