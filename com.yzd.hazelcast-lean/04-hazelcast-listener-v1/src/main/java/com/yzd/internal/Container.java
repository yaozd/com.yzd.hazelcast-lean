package com.yzd.internal;

import com.yzd.config.ContainerConfig;
import com.yzd.config.internal.ProtocolConfig;
import com.yzd.config.internal.RouterConfig;
import com.yzd.context.DuplexFlowContext;
import com.yzd.monitor.MetricsManager;
import com.yzd.verticle.SimpleRouter;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author: yaozh
 * @Description:
 */
@Slf4j
public class Container {
    public static final AtomicBoolean STATUS = new AtomicBoolean();
    private static final Container INSTANCE = new Container();
    private SimpleRouter simpleRouter;
    @Getter
    private Map<String, DuplexFlowContext> duplexFlowContextMap = new HashMap<>();
    @Getter
    private volatile RouterConfig routerConfig;
    @Getter
    private ProtocolConfig protocolConfig;
    @Getter
    @Setter
    private volatile MetricsManager metricsManager;

    public static Container getInstance() {
        return INSTANCE;
    }

    public void start(ContainerConfig containerConfig) {
        if (!STATUS.compareAndSet(false, true)) {
            log.warn("Start container ignore, because a container was started!");
            return;
        }
        startInternal(containerConfig);
        log.info("Start  container success!");
    }

    private void startInternal(ContainerConfig containerConfig) {
        this.routerConfig = containerConfig.getRouterConfig();
        this.protocolConfig = containerConfig.getProtocolConfig();
        this.simpleRouter = new SimpleRouter(this);
    }

    public void shutdown() {
        if (!STATUS.compareAndSet(true, false)) {
            log.warn("Stop container ignore, container stop!");
            return;
        }
        simpleRouter.shutdown();
    }

    public void addDuplexFlowContext(DuplexFlowContext duplexFlowContext) {
        if (duplexFlowContext != null && duplexFlowContext.isValid()) {
            duplexFlowContextMap.put(duplexFlowContext.getUuid(), duplexFlowContext);
        }
    }

    public void removeDuplexFlowContext(String uuid) {
        if (DuplexFlowContext.DEFAULT_UUID.equals(uuid)) {
            return;
        }
        duplexFlowContextMap.remove(uuid);
    }
}
