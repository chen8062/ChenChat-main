package com.chen.common.chat.service;

import com.chen.common.chat.domain.vo.request.*;
import com.chen.common.chat.domain.vo.request.member.MemberAddReq;
import com.chen.common.chat.domain.vo.request.member.MemberDelReq;
import com.chen.common.chat.domain.vo.request.member.MemberReq;
import com.chen.common.chat.domain.vo.response.ChatMemberListResp;
import com.chen.common.chat.domain.vo.response.ChatRoomResp;
import com.chen.common.chat.domain.vo.response.MemberResp;
import com.chen.common.common.domain.vo.request.CursorPageBaseReq;
import com.chen.common.common.domain.vo.response.CursorPageBaseResp;
import com.chen.common.user.domain.vo.response.ws.ChatMemberResp;

import java.util.List;

/**
 * Description:
 * Author: Chen
 * Date: 2023-07-22
 */
public interface RoomAppService {
    /**
     * 获取会话列表--支持未登录态
     */
    CursorPageBaseResp<ChatRoomResp> getContactPage(CursorPageBaseReq request, Long uid);

    /**
     * 获取群组信息
     */
    MemberResp getGroupDetail(Long uid, long roomId);

    CursorPageBaseResp<ChatMemberResp> getMemberPage(MemberReq request);

    List<ChatMemberListResp> getMemberList(ChatMessageMemberReq request);

    void delMember(Long uid, MemberDelReq request);

    void addMember(Long uid, MemberAddReq request);

    Long addGroup(Long uid, GroupAddReq request);

    ChatRoomResp getContactDetail(Long uid, Long roomId);

    ChatRoomResp getContactDetailByFriend(Long uid, Long friendUid);
}
