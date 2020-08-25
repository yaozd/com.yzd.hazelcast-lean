package com.yzd.sender;

import com.yzd.grpc.SenderGrpc;
import com.yzd.grpc.SenderProtos;
import io.grpc.stub.StreamObserver;

/**
 * @Author: yaozh
 * @Description:
 */
public class SenderService extends SenderGrpc.SenderImplBase {
    @Override
    public StreamObserver<SenderProtos.DataStreamRequest> sendStream(
            StreamObserver<SenderProtos.DataStreamResponse> responseObserver) {
        return new StreamObserver<SenderProtos.DataStreamRequest>() {
            @Override
            public void onNext(SenderProtos.DataStreamRequest dataStreamRequest) {
                responseObserver.onNext(SenderProtos.DataStreamResponse.newBuilder()
                        .setUuid(dataStreamRequest.getUuid())
                        .setIsOk(true).build());
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {

            }
        };
    }
}
