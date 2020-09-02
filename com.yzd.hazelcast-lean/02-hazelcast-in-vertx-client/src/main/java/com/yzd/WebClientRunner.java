package com.yzd;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.yzd.utils.UuIdGenerator;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;


/**
 * @Author: yaozh
 * @Description:
 */
public class WebClientRunner {
    public static void main(String[] args) {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        ch.qos.logback.classic.Logger logger = loggerContext.getLogger("root");
        logger.setLevel(Level.toLevel("INFO"));
        WebClientOptions options = new WebClientOptions()
                .setUserAgent("My-App/1.2.3");
        options.setKeepAlive(false);
        //default value:5
        options.setMaxPoolSize(1000);
        WebClient client = WebClient.create(Vertx.vertx(), options);
        call(client);
        multiThreadsCall(client);
    }

    private static void call(WebClient client) {
        client.get(6666, "localhost", "")
                .addQueryParam("uuid", UuIdGenerator.generate())
                .send(ar -> {
                    if (ar.succeeded()) {
                        // Obtain response
                        HttpResponse<Buffer> response = ar.result();
                        System.out.println("Received response with status code" + response.statusCode());
                    } else {
                        System.out.println("Something went wrong " + ar.cause().getMessage());
                    }
                });
    }

    private static void multiThreadsCall(WebClient client) {
        int nThreads = 100;
        CompletableFuture[] tasks = new CompletableFuture[nThreads];
        for (int i = 0; i < nThreads; i++) {

            tasks[i] = CompletableFuture.runAsync(new Runnable() {
                @Override
                public void run() {
                    call(client);
                }
            }, Executors.newCachedThreadPool());
        }
        CompletableFuture<Void> all = CompletableFuture.allOf(tasks);
        //等待所有异步程序处理完成
        all.join();
    }
}
