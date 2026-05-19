package com.chen.common.chat.service.strategy.mark;

import com.chen.common.common.exception.CommonErrorEnum;
import com.chen.common.common.utils.AssertUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * Description: 消息标记策略工厂
 * Author: Chen
 * Date: 2023-05-30
 */
public class MsgMarkFactory {
    private static final Map<Integer, AbstractMsgMarkStrategy> STRATEGY_MAP = new HashMap<>();

    public static void register(Integer markType, AbstractMsgMarkStrategy strategy) {
        STRATEGY_MAP.put(markType, strategy);
    }

    public static AbstractMsgMarkStrategy getStrategyNoNull(Integer markType) {
        AbstractMsgMarkStrategy strategy = STRATEGY_MAP.get(markType);
        AssertUtil.isNotEmpty(strategy, CommonErrorEnum.PARAM_VALID);
        return strategy;
    }
}
