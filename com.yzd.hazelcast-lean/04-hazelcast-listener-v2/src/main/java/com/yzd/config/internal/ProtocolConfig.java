package com.yzd.config.internal;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author: yaozh
 * @Description:
 */
@Getter
@Setter
public class ProtocolConfig {
    private Long maxRequestTimeout = Long.valueOf(60000);
}
