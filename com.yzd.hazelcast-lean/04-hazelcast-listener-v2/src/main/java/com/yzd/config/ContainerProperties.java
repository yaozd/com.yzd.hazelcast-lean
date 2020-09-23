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
@ConfigurationProperties(prefix = "listener")
public class ContainerProperties {
    private ContainerConfig container;
}