package com.yzd.grpc.transfer;

import com.yzd.grpc.transfer.TransferGrpc.TransferImplBase;

/**
 * @Author: yaozh
 * @Description:
 */
public class TransferService extends TransferImplBase {
    @Override
    public void send(com.yzd.grpc.transfer.TransferProtos.RequestData request,
                     io.grpc.stub.StreamObserver<com.yzd.grpc.transfer.TransferProtos.ResponseData> responseObserver) {
        TransferProtos.ResponseData responseData= TransferProtos.ResponseData.newBuilder()
                .setUuid(request.getUuid())
                .setIsOk(true)
                .build();
        responseObserver.onNext(responseData);
        responseObserver.onCompleted();
    }
}
