package com.yzd.config.internal;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

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
