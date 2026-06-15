package com.chen.common.chatai.handler;

import com.chen.common.chat.domain.entity.Message;
import com.chen.common.chat.domain.entity.msg.MessageExtra;
import com.chen.common.chatai.domain.ChatGPTMsg;
import com.chen.common.chatai.domain.builder.ChatGPTMsgBuilder;
import com.chen.common.chatai.dto.GPTRequestDTO;
import com.chen.common.chatai.properties.QwenProperties;
import com.chen.common.chatai.utils.ChatGPTUtils;
import com.chen.common.common.constant.RedisKey;
import com.chen.common.common.domain.dto.FrequencyControlDTO;
import com.chen.common.common.exception.FrequencyControlException;
import com.chen.common.common.service.frequencycontrol.FrequencyControlUtil;
import com.chen.common.common.utils.RedisUtils;
import com.chen.common.user.domain.vo.response.user.UserInfoResp;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.chen.common.common.constant.RedisKey.USER_GLM2_TIME_LAST;
import static com.chen.common.common.service.frequencycontrol.FrequencyControlStrategyFactory.TOTAL_COUNT_WITH_IN_FIX_TIME_FREQUENCY_CONTROLLER;

/**
 * 阿里云 Qwen（通义千问）AI 聊天处理器（单轮对话）
 *
 * @author Chen
 * @date 2024/06/13
 */
@Slf4j
@Component
public class QwenChatAIHandler extends AbstractChatAIHandler {
    /**
     * QwenChatAIHandler 限流前缀
     */
    private static final String CHAT_QWEN_FREQUENCY_PREFIX = "QwenChatAIHandler";

    private static final List<String> ERROR_MSG = Arrays.asList(
            "还摸鱼呢？你不下班我还要下班呢。。。。",
            "没给钱，矿工了。。。。",
            "服务器被你们玩儿坏了。。。。",
            "你们这群人，我都不想理你们了。。。。",
            "艾特我那是另外的价钱。。。。",
            "得加钱");

    private static final Random RANDOM = new Random();

    private static String AI_NAME;

    @Autowired
    private QwenProperties qwenProperties;

    @Override
    protected void init() {
        super.init();
        if (isUse()) {
            UserInfoResp userInfo = userService.getUserInfo(qwenProperties.getAIUserId());
            if (userInfo == null) {
                log.error("根据AIUserId:{} 找不到用户信息", qwenProperties.getAIUserId());
                throw new RuntimeException("根据AIUserId找不到用户信息");
            }
            if (StringUtils.isBlank(userInfo.getName())) {
                log.warn("根据AIUserId:{} 找到的用户信息没有name", qwenProperties.getAIUserId());
                throw new RuntimeException("根据AIUserId: " + qwenProperties.getAIUserId() + " 找到的用户没有名字");
            }
            AI_NAME = userInfo.getName();
        }
    }

    @Override
    protected boolean isUse() {
        return qwenProperties.isUse();
    }

    @Override
    public Long getChatAIUserId() {
        return qwenProperties.getAIUserId();
    }


    @Override
    protected String doChat(Message message) {
        String content = message.getContent().replace("@" + AI_NAME, "").trim();
        Long uid = message.getFromUid();
        try {
            FrequencyControlDTO frequencyControlDTO = new FrequencyControlDTO();
            frequencyControlDTO.setKey(CHAT_QWEN_FREQUENCY_PREFIX + ":" + uid);
            frequencyControlDTO.setUnit(TimeUnit.MINUTES);
            frequencyControlDTO.setCount(1);
            frequencyControlDTO.setTime(qwenProperties.getMinute().intValue());
            return FrequencyControlUtil.executeWithFrequencyControl(TOTAL_COUNT_WITH_IN_FIX_TIME_FREQUENCY_CONTROLLER, frequencyControlDTO, () -> sendRequestToQwen(new GPTRequestDTO(content, uid)));
        } catch (FrequencyControlException e) {
            return "你太快了亲爱的~过一会再来找人家~";
        } catch (Throwable e) {
            return "系统开小差啦~~";
        }
    }

    @Nullable
    private String sendRequestToQwen(GPTRequestDTO gptRequestDTO) {
        String content = gptRequestDTO.getContent();
        String text;
        try {
            // 构建单条用户消息（单轮对话，不带上下文）
            ChatGPTMsg userMsg = ChatGPTMsgBuilder.userMsg(content);
            Response response = ChatGPTUtils.create(qwenProperties.getKey())
                    .apiUrl(qwenProperties.getApiUrl())
                    .model(qwenProperties.getModelName())
                    .timeout(qwenProperties.getTimeout())
                    .message(Collections.singletonList(userMsg))
                    .send();
            text = ChatGPTUtils.parseText(response);
        } catch (Exception e) {
            log.warn("qwen doChat warn:", e);
            return getErrorText();
        }
        return text;
    }

    private static String getErrorText() {
        int index = RANDOM.nextInt(ERROR_MSG.size());
        return ERROR_MSG.get(index);
    }

    /**
     * 用户多少分钟后才能再次聊天
     */
    private Long userMinutesLater(Long uid) {
        Date lastChatTime = RedisUtils.get(RedisKey.getKey(USER_GLM2_TIME_LAST, uid), Date.class);
        if (lastChatTime == null) {
            return 0L;
        }
        long now = System.currentTimeMillis();
        long lastChatTimeMillis = lastChatTime.getTime();
        long durationMillis = now - lastChatTimeMillis;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis);
        long remainingMinutes = qwenProperties.getMinute() - minutes;
        return remainingMinutes > 0 ? remainingMinutes : 0L;
    }


    @Override
    protected boolean supports(Message message) {
        if (!qwenProperties.isUse()) {
            return false;
        }
        MessageExtra extra = message.getExtra();
        if (extra == null) {
            return false;
        }
        if (CollectionUtils.isEmpty(extra.getAtUidList())) {
            return false;
        }
        if (!extra.getAtUidList().contains(qwenProperties.getAIUserId())) {
            return false;
        }

        if (StringUtils.isBlank(message.getContent())) {
            return false;
        }
        return StringUtils.contains(message.getContent(), "@" + AI_NAME)
                && StringUtils.isNotBlank(message.getContent().replace(AI_NAME, "").trim());
    }
}
