package com.yzd.schedule;

import com.yzd.internal.Container;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;

/**
 * @Author: yaozh
 * @Description:
 */
@Slf4j
@Component
public class ResponseJob {
    @Scheduled(initialDelay = 3000, fixedDelay = 1000 * 5)
    public void doWork() {
        Iterator<Map.Entry<String, HttpServerRequest>> iterator =
                Container.getInstance().getRequestMap().entrySet().iterator();
        while (iterator.hasNext()) {
            String timeValue = new Date().toString();
            Map.Entry<String, HttpServerRequest> next = iterator.next();
            next.getValue().response().end(timeValue, new Handler<AsyncResult<Void>>() {
                @Override
                public void handle(AsyncResult<Void> asyncResult) {
                    if (asyncResult.failed()) {
                        log.warn("Failed", asyncResult.cause());
                    }
                }
            });
            iterator.remove();
        }
    }

/*    //老的方法
    @Scheduled(initialDelay = 3000, fixedDelay = 1000 * 5)
    public void doWork_old() {
        for (Map.Entry<String, HttpServerRequest> entry : Container.getInstance().getRequestMap().entrySet()) {
            String timeValue = new Date().toString();
            Handler<AsyncResult<Void>> handler = new Handler<AsyncResult<Void>>() {
                @Override
                public void handle(AsyncResult<Void> asyncResult) {
                    if (asyncResult.failed()) {
                        log.warn("Failed", asyncResult.cause());
                    }
                }
            };
            entry.getValue().response().end(timeValue, handler);
        }
    }*/
}
