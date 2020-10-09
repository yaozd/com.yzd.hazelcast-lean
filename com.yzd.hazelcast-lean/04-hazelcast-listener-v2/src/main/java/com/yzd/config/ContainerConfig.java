package com.yzd.config;

import com.yzd.config.internal.ProtocolConfig;
import com.yzd.config.internal.RouterConfig;
import com.yzd.config.internal.SessionConfig;
import com.yzd.config.internal.TransferConfig;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author: yaozh
 * @Description:
 */
@Getter
@Setter
public class ContainerConfig {
    private RouterConfig routerConfig = new RouterConfig();
    private ProtocolConfig protocolConfig = new ProtocolConfig();
    private TransferConfig transferConfig = new TransferConfig();
    private SessionConfig sessionConfig = new SessionConfig();
}
