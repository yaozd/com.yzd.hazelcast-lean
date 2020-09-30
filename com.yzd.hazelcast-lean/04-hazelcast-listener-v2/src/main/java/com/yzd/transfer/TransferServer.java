package com.yzd.transfer;

import com.yzd.config.internal.TransferConfig;
import com.yzd.internal.Container;
import io.grpc.Server;
import io.grpc.netty.NettyServerBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @Author: yaozh
 * @Description:
 */
@Slf4j
public class TransferServer {
    private final TransferConfig transferConfig;
    private final Container container;
    private Server server;

    public TransferServer(Container container) {
        this.container = container;
        this.transferConfig = container.getTransferConfig();
        //
        start();
    }

    private void start() {
        int port = transferConfig.getPort();
        log.warn("grpc port:{}", port);
        NettyServerBuilder serverBuilder = NettyServerBuilder.forPort(port);
        serverBuilder.directExecutor();
        serverBuilder.addService(new TransferService(container));
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
