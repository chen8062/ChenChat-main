package com.chen.common.chatai.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 阿里云 Qwen（通义千问）AI 配置
 *
 * @author Chen
 * @date 2024/06/13
 */
@Data
@Component
@ConfigurationProperties(prefix = "chatai.qwen")
public class QwenProperties {

    /**
     * 是否启用 Qwen
     */
    private boolean use = false;
    /**
     * 机器人用户 ID
     */
    private Long AIUserId;
    /**
     * API 地址
     */
    private String apiUrl = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";
    /**
     * 模型名称
     */
    private String modelName = "qwen-max";
    /**
     * Qwen API Key
     */
    private String key;
    /**
     * 每个用户每?分钟可以请求一次
     */
    private Long minute = 3L;
    /**
     * 超时（毫秒）
     */
    private Integer timeout = 60 * 1000;
}
