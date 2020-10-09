package com.yzd.config.internal;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author: yaozh
 * @Description:
 */
@Getter
@Setter
public class SessionConfig {
    private String consulUrl;
    private String serviceName;
    private Integer tll = 12;
    private String tags = "";
}
