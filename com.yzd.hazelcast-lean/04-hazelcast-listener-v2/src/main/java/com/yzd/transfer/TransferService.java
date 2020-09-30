package com.yzd.transfer;

import com.yzd.context.DuplexFlowContext;
import com.yzd.grpc.transfer.TransferGrpc.TransferImplBase;
import com.yzd.grpc.transfer.TransferProtos;
import com.yzd.internal.Container;
import com.yzd.internal.ContainerEvent;

/**
 * @Author: yaozh
 * @Description:
 */
public class TransferService extends TransferImplBase {
    private final Container container;

    public TransferService(Container container) {
        super();
        this.container = container;
    }

    @Override
    public void send(TransferProtos.RequestData request,
                     io.grpc.stub.StreamObserver<TransferProtos.ResponseData> responseObserver) {
        String uuid = request.getUuid();
        if (uuid == null) {
            responseObserver.onError(new IllegalArgumentException("UUID NOT FOUND!"));
            return;
        }
        boolean isOk = false;
        DuplexFlowContext duplexFlowContext = container.getDuplexFlowContextMap().get(uuid);
        if (duplexFlowContext != null) {
            isOk = true;
            duplexFlowContext.setTargetStatus(request.getStatusCode());
            ContainerEvent.fireEntryOutputComplete(duplexFlowContext, request.getReturnBody());
        }
        TransferProtos.ResponseData responseData = TransferProtos.ResponseData.newBuilder()
                .setUuid(uuid)
                .setIsOk(isOk)
                .build();
        responseObserver.onNext(responseData);
        responseObserver.onCompleted();
    }
}
