package com.yzd.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.shell.term.HttpTermOptions;
import io.vertx.ext.shell.term.TermServer;

public class SimpleHttpTerm  extends AbstractVerticle {
    @Override
    public void start(){
        TermServer server = TermServer.
                createHttpTermServer(vertx, new HttpTermOptions().setPort(5000));
        server.termHandler(term -> {
            term.stdinHandler(new CommandHandler(term));
        }).listen();
        System.out.println("Open url: http://localhost:5000/shell.html");
    }
}
