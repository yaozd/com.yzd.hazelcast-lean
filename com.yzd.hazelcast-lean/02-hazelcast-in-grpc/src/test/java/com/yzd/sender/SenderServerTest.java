package com.yzd.sender;

import com.yzd.grpc.SenderGrpc;
import com.yzd.grpc.SenderProtos;
import io.grpc.ManagedChannel;
import io.grpc.netty.NettyChannelBuilder;
import io.grpc.stub.StreamObserver;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * @Author: yaozh
 * @Description:
 */
@Slf4j
public class SenderServerTest {

    @Test
    public void client() throws InterruptedException {
        EventLoopGroup senderGroup = new NioEventLoopGroup(1);
        NettyChannelBuilder channelBuilder = NettyChannelBuilder.forAddress("localhost", 8899)
                .directExecutor()
                .eventLoopGroup(senderGroup)
                .maxInboundMessageSize(1024 * 1024 * 500).usePlaintext();
        ManagedChannel channel = channelBuilder.build();
        //双向流式通信
        StreamObserver<SenderProtos.DataStreamRequest> requestObserver =
                SenderGrpc.newStub(channel).sendStream(new StreamObserver<SenderProtos.DataStreamResponse>() {
                    @Override
                    public void onNext(SenderProtos.DataStreamResponse response) {
                        log.info("UUID:{},isOk:{}", response.getUuid(), response.getIsOk());
                    }

                    @Override
                    public void onError(Throwable t) {
                        log.error("error!", t);
                    }

                    @Override
                    public void onCompleted() {
                    }
                });
        for (int i = 0; i < 1000; i++) {
            requestObserver.onNext(SenderProtos.DataStreamRequest.newBuilder()
                    .setUuid(String.valueOf(i)).setRequestInfo("hello world!").build());
        }
        requestObserver.onCompleted();
        Thread.currentThread().join();
    }
}