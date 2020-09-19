package com.yzd;

import com.yzd.verticle.SimpleHttpTerm;
import io.vertx.core.Vertx;

public class StartUpHttpShell {
    public static void main(String[] args) throws InterruptedException {
        Vertx.vertx().deployVerticle(new SimpleHttpTerm());
        Thread.currentThread().join();
    }
}
