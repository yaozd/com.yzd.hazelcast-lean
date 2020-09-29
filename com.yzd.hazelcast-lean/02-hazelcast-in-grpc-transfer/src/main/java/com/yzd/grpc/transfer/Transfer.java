package com.yzd.grpc.transfer;

import io.grpc.Server;
import io.grpc.netty.NettyServerBuilder;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @Author: yaozh
 * @Description:
 */
public class Transfer {
    private final int port;
    private Server server;

    public Transfer(int port) {
        this.port = port;
    }

    public void init() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(4);
        NettyServerBuilder serverBuilder = NettyServerBuilder.forPort(port);
        serverBuilder.bossEventLoopGroup(bossGroup);
        serverBuilder.workerEventLoopGroup(workerGroup);
        serverBuilder.directExecutor();
        serverBuilder.addService(new TransferService());
        serverBuilder.maxInboundMessageSize(1024 * 1024 * 500);
        serverBuilder.handshakeTimeout(10, TimeUnit.SECONDS);
        server = serverBuilder.build();
        try {
            server.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void shutdown() {
        if (server != null) {
            server.shutdown();
        }
    }
}
