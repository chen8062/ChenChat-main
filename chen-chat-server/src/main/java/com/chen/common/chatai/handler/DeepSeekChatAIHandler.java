package com.chen.common.chatai.handler;

import com.chen.common.chat.domain.entity.Message;
import com.chen.common.chat.domain.entity.msg.MessageExtra;
import com.chen.common.chatai.domain.ChatGPTContext;
import com.chen.common.chatai.domain.ChatGPTMsg;
import com.chen.common.chatai.domain.builder.ChatGPTContextBuilder;
import com.chen.common.chatai.domain.builder.ChatGPTMsgBuilder;
import com.chen.common.chatai.properties.DeepSeekProperties;
import com.chen.common.chatai.utils.ChatGPTUtils;
import com.chen.common.common.constant.RedisKey;
import com.chen.common.common.domain.dto.FrequencyControlDTO;
import com.chen.common.common.exception.FrequencyControlException;
import com.chen.common.common.service.frequencycontrol.FrequencyControlUtil;
import com.chen.common.common.utils.DateUtils;
import com.chen.common.common.utils.RedisUtils;
import com.chen.common.user.domain.vo.response.user.UserInfoResp;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.chen.common.common.constant.RedisKey.USER_CHAT_CONTEXT;
import static com.chen.common.common.service.frequencycontrol.FrequencyControlStrategyFactory.TOTAL_COUNT_WITH_IN_FIX_TIME_FREQUENCY_CONTROLLER;

/**
 * DeepSeek AI 聊天处理器（支持多轮对话）
 *
 * @author Chen
 * @date 2024/06/13
 */
@Slf4j
@Component
public class DeepSeekChatAIHandler extends AbstractChatAIHandler {
    /**
     * DeepSeekChatAIHandler 限流前缀
     */
    private static final String CHAT_FREQUENCY_PREFIX = "DeepSeekChatAIHandler";

    @Autowired
    private DeepSeekProperties deepSeekProperties;

    private static String AI_NAME;

    @Override
    protected void init() {
        super.init();
        if (isUse()) {
            UserInfoResp userInfo = userService.getUserInfo(deepSeekProperties.getAIUserId());
            if (userInfo == null) {
                log.error("根据AIUserId:{} 找不到用户信息", deepSeekProperties.getAIUserId());
                throw new RuntimeException("根据AIUserId: " + deepSeekProperties.getAIUserId() + " 找不到用户信息");
            }
            if (StringUtils.isBlank(userInfo.getName())) {
                log.warn("根据AIUserId:{} 找到的用户信息没有name", deepSeekProperties.getAIUserId());
                throw new RuntimeException("根据AIUserId: " + deepSeekProperties.getAIUserId() + " 找到的用户没有名字");
            }
            AI_NAME = userInfo.getName();
        }
    }

    @Override
    protected boolean isUse() {
        return deepSeekProperties.isUse();
    }

    @Override
    public Long getChatAIUserId() {
        return deepSeekProperties.getAIUserId();
    }

    @Override
    protected String doChat(Message message) {
        Long uid = message.getFromUid();
        try {
            FrequencyControlDTO frequencyControlDTO = new FrequencyControlDTO();
            frequencyControlDTO.setKey(RedisKey.getKey(CHAT_FREQUENCY_PREFIX) + ":" + uid);
            frequencyControlDTO.setUnit(TimeUnit.HOURS);
            frequencyControlDTO.setCount(deepSeekProperties.getLimit());
            frequencyControlDTO.setTime(1);
            return FrequencyControlUtil.executeWithFrequencyControl(TOTAL_COUNT_WITH_IN_FIX_TIME_FREQUENCY_CONTROLLER,
                    frequencyControlDTO,
                    () -> sendRequestToDeepSeek(message));
        } catch (FrequencyControlException e) {
            return "亲爱的,你今天找我聊了" + deepSeekProperties.getLimit() + "次了~人家累了~明天见";
        } catch (Throwable e) {
            return "系统开小差啦~~";
        }
    }


    private String sendRequestToDeepSeek(Message message) {
        ChatGPTContext context = buildContext(message);
        context = tailorContext(context);
        log.info("context = {}", context);
        String text;
        try {
            Response response = ChatGPTUtils.create(deepSeekProperties.getKey())
                    .apiUrl(deepSeekProperties.getApiUrl())
                    .proxyUrl(deepSeekProperties.getProxyUrl())
                    .model(deepSeekProperties.getModelName())
                    .timeout(deepSeekProperties.getTimeout())
                    .maxTokens(deepSeekProperties.getMaxTokens())
                    .message(context.getMsg())
                    .send();
            text = ChatGPTUtils.parseText(response);
            ChatGPTMsg chatGPTMsg = ChatGPTMsgBuilder.assistantMsg(text);
            context.addMsg(chatGPTMsg);
            saveContext(context);
        } catch (Exception e) {
            log.warn("deepseek doChat warn:", e);
            text = "我累了，明天再聊吧";
        }
        return text;
    }

    private ChatGPTContext tailorContext(ChatGPTContext context) {
        List<ChatGPTMsg> msg = context.getMsg();
        Integer integer = ChatGPTUtils.countTokens(msg);
        if (integer < (deepSeekProperties.getMaxTokens() - 500)) {
            return context;
        }
        msg.remove(1);
        return tailorContext(context);
    }

    private ChatGPTContext buildContext(Message message) {
        String prompt = message.getContent().replace("@" + AI_NAME, "").trim();
        Long uid = message.getFromUid();
        Long roomId = message.getRoomId();
        ChatGPTContext chatGPTContext = RedisUtils.get(RedisKey.getKey(USER_CHAT_CONTEXT, uid, roomId), ChatGPTContext.class);
        if (chatGPTContext == null) {
            chatGPTContext = ChatGPTContextBuilder.initContext(uid, roomId);
        }
        saveContext(chatGPTContext);
        chatGPTContext.addMsg(ChatGPTMsgBuilder.userMsg(prompt));
        return chatGPTContext;
    }

    private void saveContext(ChatGPTContext chatGPTContext) {
        RedisUtils.set(RedisKey.getKey(USER_CHAT_CONTEXT, chatGPTContext.getUid(), chatGPTContext.getRoomId()), chatGPTContext, 5L, TimeUnit.MINUTES);
    }

    @Override
    protected boolean supports(Message message) {
        if (!deepSeekProperties.isUse()) {
            return false;
        }
        MessageExtra extra = message.getExtra();
        if (extra == null) {
            return false;
        }
        if (CollectionUtils.isEmpty(extra.getAtUidList())) {
            return false;
        }
        if (!extra.getAtUidList().contains(deepSeekProperties.getAIUserId())) {
            return false;
        }

        if (StringUtils.isBlank(message.getContent())) {
            return false;
        }
        return StringUtils.contains(message.getContent(), "@" + AI_NAME)
                && StringUtils.isNotBlank(message.getContent().replace(AI_NAME, "").trim());
    }
}
