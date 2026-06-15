package com.chen.common.chatai.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * DeepSeek AI 配置
 *
 * @author Chen
 * @date 2024/06/13
 */
@Data
@Component
@ConfigurationProperties(prefix = "chatai.deepseek")
public class DeepSeekProperties {

    /**
     * 是否启用 DeepSeek
     */
    private boolean use = false;
    /**
     * 机器人用户 ID
     */
    private Long AIUserId;
    /**
     * API 地址
     */
    private String apiUrl = "https://api.deepseek.com/v1/chat/completions";
    /**
     * 模型名称
     */
    private String modelName = "deepseek-v4-pro";
    /**
     * DeepSeek API Key
     */
    private String key;
    /**
     * 代理地址（可选）
     */
    private String proxyUrl;
    /**
     * 超时（毫秒）
     */
    private Integer timeout = 60 * 1000;
    /**
     * 用户每小时条数限制
     */
    private Integer limit = 20;
    /**
     * 最大令牌数
     */
    private Integer maxTokens = 2048;
}
