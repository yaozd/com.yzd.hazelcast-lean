package com.yzd.verticle;

/**
 * @Author: yaozh
 * @Description:
 */

import com.yzd.common.StateEnum;
import com.yzd.context.DuplexFlowContext;
import com.yzd.internal.Container;
import com.yzd.internal.ContainerEvent;
import io.vertx.core.*;
import io.vertx.core.http.HttpServer;
import io.vertx.core.impl.cpu.CpuCoreSensor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * 简单的路由使用
 *
 * @author yaozh
 */
@Slf4j
public class SimpleRouter extends AbstractVerticle {
    public static final String UUID_KEY = "uuid";
    private final Container container;
    private final int port;

    public SimpleRouter(Container container) {
        this.container = container;
        this.port = this.container.getRouterConfig().getPort();
        init();
    }

    private void init() {
        VertxOptions vertxOptions = new VertxOptions()
                .setWorkerPoolSize(5).setEventLoopPoolSize(CpuCoreSensor.availableProcessors());
        Vertx.vertx(vertxOptions).deployVerticle(this);
    }

    @Override
    public void start() throws Exception {
        // 创建HttpServer
        HttpServer server = vertx.createHttpServer();
        vertx.exceptionHandler(throwable -> {
            log.error("Vertx inner error!", throwable);
            container.getMetricsManager().incrementException("Unknown-Vertx");
        });
        server.requestHandler(req -> {
            DuplexFlowContext duplexFlowContext = new DuplexFlowContext(this.container, req);
            ContainerEvent.fireEntryInput(duplexFlowContext);
            String uuid = req.getParam(UUID_KEY);
            /*String uuid = req.headers().get(UUID_KEY);*/
            if (StringUtils.isEmpty(uuid)) {
                ContainerEvent.fireInterruptRequest(
                        duplexFlowContext, StateEnum.UUID_NOT_FOUND, "NOT FOUND UUID !");
                return;
            }
            //单一请求连接事件
            req.connection().closeHandler(new Handler<Void>() {
                @Override
                public void handle(Void aVoid) {
                    ContainerEvent.fireInterruptRequest(
                            duplexFlowContext, StateEnum.CLIENT_CLOSED_CONNECTION, "Client close connection !");
                    container.removeDuplexFlowContext(uuid);
                    log.info("Close! request event, uuid:{}", uuid);
                }
            });
            req.exceptionHandler(throwable -> {
                log.error("Exception! request event, uuid:{}", uuid, throwable);
                ContainerEvent.fireInterruptRequest(duplexFlowContext, StateEnum.REQUEST_HANDLER_ERROR, "Inner error !");
                container.removeDuplexFlowContext(uuid);
            });
            container.addDuplexFlowContext(uuid, duplexFlowContext);
            try {
                //todo 业务逻辑处理
                //模拟业务逻辑处理异常！！
                int a = 0;
                int i = 1 / a;
            } catch (Exception ex) {
                ContainerEvent.fireInterruptRequest(duplexFlowContext, StateEnum.BUSINESS_HANDLER_ERROR, "Business handler error!");
                container.removeDuplexFlowContext(uuid);
                log.error("Exception! request event, uuid:{}", uuid, ex);
            }
        });
        //TCP连接事件
        server.connectionHandler(httpConnection -> {
            container.getMetricsManager().incrementConnectionGauge();
            if (log.isDebugEnabled()) {
                log.debug("Connection! connection open.");
            }
            httpConnection.closeHandler(closeVoid -> {
                container.getMetricsManager().decrementConnectionGauge();
                if (log.isDebugEnabled()) {
                    log.debug("Close! connection event.");
                }
            });
        });
        //连接异常：远程强制关闭
        server.exceptionHandler(throwable -> {
            log.error("Unknown exception!", throwable);
            container.getMetricsManager().incrementException("Unknown-Server");
        });
        Handler<AsyncResult<HttpServer>> listenHandler = new Handler<AsyncResult<HttpServer>>() {
            @Override
            public void handle(AsyncResult<HttpServer> httpServerAsyncResult) {
                if (httpServerAsyncResult.failed()) {
                    log.warn("Listen failed!", httpServerAsyncResult.cause());
                    return;
                }
                log.info("The Listener server started successfully! bind to port [{}]  http://localhost:{}/ ",
                        port, port);
            }
        };
        server.listen(port, listenHandler);
    }

    public void shutdown() {
        vertx.close();
        vertx.nettyEventLoopGroup().shutdownGracefully();
    }
}