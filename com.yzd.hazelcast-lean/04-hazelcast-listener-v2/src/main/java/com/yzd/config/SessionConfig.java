package com.yzd.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Author: yaozh
 * @Description:
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "listener.session")
public class SessionConfig {
    private String consulUrl;
    private String serviceName;
    private Integer tll = 12;
    private String tags = "";
}
