package com.yzd.grpc.transfer;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * @Author: yaozh
 * @Description:
 */
@Slf4j
public class TransferClientTest {
    private ManagedChannel channel;
    private ManagedChannelBuilder<?> channelBuilder;
    @Before
    public void init() {
        //
        channelBuilder = ManagedChannelBuilder
                .forAddress("localhost", 30200)
                .maxInboundMessageSize(1024 * 1024 * 20)
                //发起RST_STREAM 帧（RST_STREAM 类型的 frame，可以在不断开连接的前提下取消某个 request 的 stream）：
                //通过keepAliveTime与keepAliveTimeout的时间调整,可以模拟RST_STREAM 帧
                .keepAliveTime(10, TimeUnit.MINUTES)
                .keepAliveTimeout(10, TimeUnit.MINUTES)
                .idleTimeout(10, TimeUnit.MINUTES)
                .enableFullStreamDecompression()
                .usePlaintext();
        channel = channelBuilder.build();
    }
    @Test
    public void call(){
        TransferGrpc.TransferBlockingStub transferBlockingStub = TransferGrpc.newBlockingStub(channel);
        TransferProtos.RequestData requestData= TransferProtos.RequestData.newBuilder()
                .setUuid("TEST")
                .setStatusCode(200)
                .setReturnBody(String.valueOf(System.currentTimeMillis())).build();
        TransferProtos.ResponseData responseData = transferBlockingStub.send(requestData);
        log.info(responseData.getUuid()+":"+responseData.getIsOk());
    }
}
