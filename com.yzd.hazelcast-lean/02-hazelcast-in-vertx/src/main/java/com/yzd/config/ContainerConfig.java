package com.yzd.config;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author: yaozh
 * @Description:
 */
@Getter
@Setter
public class ContainerConfig {
    private int routerPort = Integer.parseInt(System.getProperty("router.port", "8888"));
}
