package com.yzd.internal;

import com.yzd.config.ContainerConfig;
import com.yzd.config.internal.ProtocolConfig;
import com.yzd.config.internal.RouterConfig;
import com.yzd.config.internal.SessionConfig;
import com.yzd.config.internal.TransferConfig;
import com.yzd.context.DuplexFlowContext;
import com.yzd.hazelcast.HazelcastSessionStorage;
import com.yzd.hazelcast.SessionStorage;
import com.yzd.monitor.MetricsManager;
import com.yzd.rocketmq.RocketmqConsumer;
import com.yzd.transfer.TransferClientManager;
import com.yzd.transfer.TransferServer;
import com.yzd.verticle.SimpleRouter;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author: yaozh
 * @Description:
 */
@Slf4j
public class Container {
    public static final AtomicBoolean STATUS = new AtomicBoolean();
    private static final Container INSTANCE = new Container();
    private static final int ONE_SECOND = 1;
    private SimpleRouter simpleRouter;
    private ScheduledExecutorService executorService;
    @Getter
    private Map<String, DuplexFlowContext> duplexFlowContextMap = new ConcurrentHashMap<>();
    @Getter
    private volatile RouterConfig routerConfig;
    @Getter
    private volatile ProtocolConfig protocolConfig;
    @Getter
    private volatile TransferConfig transferConfig;
    private TransferServer transferServer;
    @Getter
    private volatile SessionConfig sessionConfig;
    @Getter
    private volatile SessionStorage sessionStorage;
    @Getter
    @Setter
    private volatile MetricsManager metricsManager;
    @Getter
    private volatile TransferClientManager transferClientManager = new TransferClientManager();
    private volatile RocketmqConsumer rocketmqConsumer;


    public static Container getInstance() {
        return INSTANCE;
    }

    public void start(ContainerConfig containerConfig) {
        if (!STATUS.compareAndSet(false, true)) {
            log.warn("Start container ignore, because a container was started!");
            return;
        }
        startInternal(containerConfig);
        this.executorService = new ScheduledThreadPoolExecutor(1, r -> {
            Thread thread = new Thread(r, "T-Container-Metrics-Job");
            //thread.setDaemon(true);
            return thread;
        });
        this.executorService.scheduleAtFixedRate(this::metricsJob, ONE_SECOND, ONE_SECOND, TimeUnit.SECONDS);
        log.info("Start  container success!");
    }

    private void metricsJob() {
        metricsManager.activeRequestContextGauge(duplexFlowContextMap.size());
        metricsManager.activeLocalSessionGauge(sessionStorage.size());
    }

    private void startInternal(ContainerConfig containerConfig) {
        this.routerConfig = containerConfig.getRouterConfig();
        this.protocolConfig = containerConfig.getProtocolConfig();
        this.transferConfig = containerConfig.getTransferConfig();
        this.sessionConfig = containerConfig.getSessionConfig();
        this.simpleRouter = new SimpleRouter(this);
        this.transferServer = new TransferServer(this);
        this.sessionStorage = new HazelcastSessionStorage(this);
        this.rocketmqConsumer=new RocketmqConsumer(this);
    }

    public void shutdown() {
        if (!STATUS.compareAndSet(true, false)) {
            log.warn("Stop container ignore, container stop!");
            return;
        }
        sessionStorage.shutdown();
        simpleRouter.shutdown();
        transferServer.shutdown();
        transferClientManager.shutdown();
    }

    public void addDuplexFlowContext(DuplexFlowContext duplexFlowContext) {
        if (duplexFlowContext != null && duplexFlowContext.isValid()) {
            duplexFlowContextMap.put(duplexFlowContext.getUuid(), duplexFlowContext);
            sessionStorage.addSessionId(duplexFlowContext.getUuid());
        }
    }

    public void removeDuplexFlowContext(String uuid) {
        if (DuplexFlowContext.DEFAULT_UUID.equals(uuid)) {
            return;
        }
        duplexFlowContextMap.remove(uuid);
        sessionStorage.removeSessionId(uuid);
    }
}
