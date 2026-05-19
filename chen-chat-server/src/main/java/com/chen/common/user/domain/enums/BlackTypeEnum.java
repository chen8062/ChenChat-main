package com.chen.common.user.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Description: 物品枚举
 * Author: Chen
 * Date: 2023-03-19
 */
@AllArgsConstructor
@Getter
public enum BlackTypeEnum {
    IP(1),
    UID(2),
    ;

    private final Integer type;

}
