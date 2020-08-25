package com.yzd.verticle;

/**
 * @Author: yaozh
 * @Description:
 */

import com.yzd.internal.Container;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * 简单的路由使用
 */
@Slf4j
public class SimpleRouter extends AbstractVerticle {
    private final Container container;

    public SimpleRouter(Container container) {
        this.container = container;
        init();
    }

    private void init() {
        Vertx.vertx().deployVerticle(this);
    }

    @Override
    public void start() throws Exception {
        // 创建HttpServer
        HttpServer server = vertx.createHttpServer();
        server.requestHandler(req -> {
            String uuid = req.headers().get("uuid");
            if (StringUtils.isEmpty(uuid)) {
                req.response().end("UUID NOT FOUND!");
                return;
            }
            container.addRequest(uuid, req);
            req.connection().closeHandler(new Handler<Void>() {
                @Override
                public void handle(Void aVoid) {
                    container.removeRequest(uuid);
                    log.info("Close uuid:{}", uuid);
                }
            });
        });

        Handler<AsyncResult<HttpServer>> listenHandler = new Handler<AsyncResult<HttpServer>>() {
            @Override
            public void handle(AsyncResult<HttpServer> httpServerAsyncResult) {
                if (httpServerAsyncResult.failed()) {
                    log.warn("Listen failed!", httpServerAsyncResult.cause());
                    return;
                }
                log.info("Start simple router success! listen http://localhost:{}/ ",container.getContainerConfig().getRouterPort());
            }
        };
        server.listen(container.getContainerConfig().getRouterPort(), listenHandler);
    }

//    @Override
//    public void start() throws Exception {
//        // 创建HttpServer
//        HttpServer server = vertx.createHttpServer();
//        // 创建路由对象
//        Router router = Router.router(vertx);
//        // 监听/index地址
//        router.route("/index").handler(request -> {
//            request.response().end("INDEX SUCCESS");
//        });
//        router.route("/").handler(request -> {
//            request.response().end("Hello world");
//        });
//        // 把请求交给路由处理--------------------(1)
//        server.requestHandler(router::accept);
//        server.listen(8888);
//    }
}