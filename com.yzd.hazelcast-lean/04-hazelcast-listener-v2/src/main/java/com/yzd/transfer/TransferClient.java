package com.yzd.transfer;

import com.yzd.grpc.transfer.TransferGrpc;
import com.yzd.grpc.transfer.TransferProtos;
import com.yzd.hazelcast.NodeInfo;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @Author: yaozh
 * @Description:
 */
@Slf4j
public class TransferClient {
    private ManagedChannel channel;
    private TransferGrpc.TransferBlockingStub transferBlockingStub;
    private ManagedChannelBuilder<?> channelBuilder;

    public TransferClient(NodeInfo memberInfo) {
        channelBuilder = ManagedChannelBuilder
                .forAddress(memberInfo.getIp(), memberInfo.getGrpcPort())
                .maxInboundMessageSize(1024 * 1024 * 20)
                //发起RST_STREAM 帧（RST_STREAM 类型的 frame，可以在不断开连接的前提下取消某个 request 的 stream）：
                //通过keepAliveTime与keepAliveTimeout的时间调整,可以模拟RST_STREAM 帧
                .keepAliveTime(10, TimeUnit.MINUTES)
                .keepAliveTimeout(10, TimeUnit.MINUTES)
                .idleTimeout(10, TimeUnit.MINUTES)
                .enableFullStreamDecompression()
                .usePlaintext();
        channel = channelBuilder.build();
        transferBlockingStub = TransferGrpc.newBlockingStub(channel);
    }

    public boolean call(String uuid, int statusCode, String replyBody) {
        TransferProtos.RequestData requestData = TransferProtos.RequestData.newBuilder()
                .setUuid(uuid)
                .setStatusCode(statusCode)
                .setReturnBody(replyBody).build();
        TransferProtos.ResponseData responseData = transferBlockingStub.send(requestData);
        log.info(responseData.getUuid() + ":" + responseData.getIsOk());
        return responseData.getIsOk();
    }

    protected void shutdown() {
        channel.shutdown();
    }
}
