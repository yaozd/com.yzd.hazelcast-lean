package com.yzd.verticle;

/**
 * @Author: yaozh
 * @Description:
 */

import com.yzd.context.DuplexFlowContext;
import com.yzd.internal.Container;
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
    private final Container container;

    public SimpleRouter(Container container) {
        this.container = container;
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
        server.requestHandler(req -> {
            String uuid = req.getParam("uuid");
            //String uuid = req.headers().get("uuid");
            if (StringUtils.isEmpty(uuid)) {
                req.response().end("UUID NOT FOUND!");
                return;
            }
            DuplexFlowContext duplexFlowContext = new DuplexFlowContext(this.container, req);
            container.addDuplexFlowContext(uuid, duplexFlowContext);
            //单一请求连接事件
            req.connection().closeHandler(new Handler<Void>() {
                @Override
                public void handle(Void aVoid) {
                    container.removeDuplexFlowContext(uuid);
                    log.info("Close! request event, uuid:{}", uuid);
                }
            });
        });
        //TCP连接事件
        server.connectionHandler(httpConnection -> {
            log.warn("Connection! connection open.");
            httpConnection.closeHandler(closeVoid -> {
                log.warn("Close! connection event.");
            });
        });
        Handler<AsyncResult<HttpServer>> listenHandler = new Handler<AsyncResult<HttpServer>>() {
            @Override
            public void handle(AsyncResult<HttpServer> httpServerAsyncResult) {
                if (httpServerAsyncResult.failed()) {
                    log.warn("Listen failed!", httpServerAsyncResult.cause());
                    return;
                }
                log.info("Start simple router success! listen http://localhost:{}/ ",
                        container.getRouterConfig().getPort());
            }
        };
        server.listen(container.getRouterConfig().getPort(), listenHandler);
    }

    public void shutdown() {
        vertx.close();
        vertx.nettyEventLoopGroup().shutdownGracefully();
    }
}