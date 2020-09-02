package com.yzd.verticle;

/**
 * @Author: yaozh
 * @Description:
 */

import com.yzd.internal.Container;
import io.vertx.core.*;
import io.vertx.core.http.HttpServer;
import io.vertx.core.impl.cpu.CpuCoreSensor;
import lombok.extern.slf4j.Slf4j;

/**
 * 简单的路由使用
 *  请求处理异常，解码失败异常，远程强制关闭异常等
 * @author yaozh
 */
@Slf4j
public class SimpleRouterV2 extends AbstractVerticle {
    public static final String UUID_KEY = "uuid";
    private final Container container;
    private final int port;

    public SimpleRouterV2(Container container) {
        this.container = container;
        this.port = 8899;
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
            log.error("Unknown vertx error!", throwable);
        });
        server.requestHandler(req -> {
            //统计body的流量与body的内容
            req.handler(data -> {
                int capacity = data.getByteBuf().capacity();
                log.info("Payload:{}", capacity);
            });
            //查询不到值，byteRead暂时无用
            log.info("BytesRead:{}", req.bytesRead());
//            DuplexFlowContext duplexFlowContext = new DuplexFlowContext(this.container, req);
//            ContainerEvent.fireEntryInput(duplexFlowContext);
//            if (!duplexFlowContext.isValid()) {
//                ContainerEvent.fireInterruptRequest(
//                        duplexFlowContext, StateEnum.UUID_NOT_FOUND, "Not found uuid!");
//                return;
//            }
            //单一请求连接事件
            req.connection().closeHandler(new Handler<Void>() {
                @Override
                public void handle(Void aVoid) {
//                    log.warn("Close! request event, uuid:{}", duplexFlowContext.getUuid());
//                    ContainerEvent.fireInterruptRequest(
//                            duplexFlowContext, StateEnum.CLIENT_CLOSED_CONNECTION, "Client close connection!");
                }
            });
            req.exceptionHandler(throwable -> {
//                log.error("Exception! request event, uuid:{}", duplexFlowContext.getUuid(), throwable);
//                ContainerEvent.fireInterruptRequest(
//                        duplexFlowContext, StateEnum.REQUEST_HANDLER_ERROR, throwable.toString());
            });
//            container.addDuplexFlowContext(duplexFlowContext);
            try {
                //todo 业务逻辑处理
                //模拟业务逻辑处理异常！！
                int a = 0;
                //int i = 1 / a;
            } catch (Throwable throwable) {
//                log.error("Exception! request event, uuid:{}", duplexFlowContext.getUuid(), throwable);
//                ContainerEvent.fireInterruptRequest(
//                        duplexFlowContext, StateEnum.BUSINESS_HANDLER_ERROR, throwable.toString());
            }
        });
        //TCP连接事件
        server.connectionHandler(httpConnection -> {

//            container.getMetricsManager().incrementConnectionGauge();
            if (log.isDebugEnabled()) {
                log.debug("Connection! connection open.");
            }
            httpConnection.closeHandler(closeVoid -> {
//                container.getMetricsManager().decrementConnectionGauge();
                if (log.isDebugEnabled()) {
                    log.debug("Close! connection event.");
                }
            });
        });
        //连接异常：远程强制关闭
        server.exceptionHandler(throwable -> {
            log.error("Unknown server exception!", throwable);
//            container.getMetricsManager().incrementException("Unknown-Server");
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