package com.chen.common.chat.service.cache;

import com.chen.common.chat.dao.GroupMemberDao;
import com.chen.common.chat.dao.MessageDao;
import com.chen.common.chat.dao.RoomGroupDao;
import com.chen.common.chat.domain.entity.RoomGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * Description: 群成员相关缓存
 * Author: Chen
 * Date: 2023-03-27
 */
@Component
public class GroupMemberCache {

    @Autowired
    private MessageDao messageDao;
    @Autowired
    private RoomGroupDao roomGroupDao;
    @Autowired
    private GroupMemberDao groupMemberDao;

    @Cacheable(cacheNames = "member", key = "'groupMember'+#roomId")
    public List<Long> getMemberUidList(Long roomId) {
        RoomGroup roomGroup = roomGroupDao.getByRoomId(roomId);
        if (Objects.isNull(roomGroup)) {
            return null;
        }
        return groupMemberDao.getMemberUidList(roomGroup.getId());
    }

    @CacheEvict(cacheNames = "member", key = "'groupMember'+#roomId")
    public List<Long> evictMemberUidList(Long roomId) {
        return null;
    }

}
