package com.yzd.config;

import com.yzd.config.internal.ProtocolConfig;
import com.yzd.config.internal.RouterConfig;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author: yaozh
 * @Description:
 */
@Getter
@Setter
public class ContainerConfig {
    private RouterConfig routerConfig;
    private ProtocolConfig protocolConfig;
}
