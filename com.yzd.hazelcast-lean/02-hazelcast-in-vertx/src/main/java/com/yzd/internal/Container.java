package com.yzd.internal;

import com.yzd.config.ContainerConfig;
import com.yzd.verticle.SimpleRouter;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author: yaozh
 * @Description:
 */
@Slf4j
public class Container {
    private static final Container INSTANCE = new Container();
    public static final AtomicBoolean STATUS = new AtomicBoolean();
    private SimpleRouter simpleRouter;
    @Getter
    private ContainerConfig containerConfig;
    @Getter
    private Map<String, HttpServerRequest> requestMap=new HashMap<>();
    public static Container getInstance() {
        return INSTANCE;
    }

    public void start() {
        if (!STATUS.compareAndSet(false, true)) {
            log.warn("Start container ignore, because a container was started!");
            return;
        }
        this.containerConfig=new ContainerConfig();
        startInternal();
        log.info("Start  container success!");
    }

    private void startInternal() {
        this.simpleRouter=new SimpleRouter(this);
    }

    public void shutdown() {
        if (!STATUS.compareAndSet(true, false)) {
            log.warn("Stop container ignore, container stop!");
            return;
        }
    }
    public void addRequest(String uuid,HttpServerRequest request){
        requestMap.put(uuid,request);
    }

    public void removeRequest(String uuid) {
        requestMap.remove(uuid);
    }
}
