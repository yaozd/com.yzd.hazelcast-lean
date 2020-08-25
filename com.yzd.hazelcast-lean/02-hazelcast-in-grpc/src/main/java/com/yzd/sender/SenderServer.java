package com.yzd.sender;

import io.grpc.Server;
import io.grpc.netty.NettyServerBuilder;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @Author: yaozh
 * @Description:
 */
@Slf4j
public class SenderServer {
    private static final int port = Integer.parseInt(System.getProperty("sender.port", "8899"));

    public void init() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(4);
        NettyServerBuilder serverBuilder = NettyServerBuilder.forPort(port);
        serverBuilder.bossEventLoopGroup(bossGroup);
        serverBuilder.workerEventLoopGroup(workerGroup);
        serverBuilder.directExecutor();
        serverBuilder.addService(new SenderService());
        serverBuilder.flowControlWindow(1000000000);
        //模拟PING
        serverBuilder.permitKeepAliveWithoutCalls(true);
        serverBuilder.keepAliveTime(10, TimeUnit.SECONDS);
        //PING
        serverBuilder.maxInboundMessageSize(1024 * 1024 * 500);
        serverBuilder.handshakeTimeout(10, TimeUnit.SECONDS);
        Server server = serverBuilder.build();
        try {
            server.start();
        } catch (IOException e) {
            log.error("Sender server init failed!", e);
            throw new RuntimeException(e);
        }
    }
}
