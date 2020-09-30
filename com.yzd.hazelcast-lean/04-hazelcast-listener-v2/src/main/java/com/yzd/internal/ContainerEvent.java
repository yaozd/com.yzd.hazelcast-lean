package com.yzd.internal;

import com.yzd.common.StateEnum;
import com.yzd.context.DuplexFlowContext;

/**
 * @Author: yaozh
 * @Description:
 */
public class ContainerEvent {

    public static void fireEntryInput(DuplexFlowContext duplexFlowContext) {
        duplexFlowContext.getContainer().getMetricsManager()
                .incrementPayloadCounter(duplexFlowContext.getServiceName(), duplexFlowContext.getPayload());
        duplexFlowContext.getContainer().getMetricsManager()
                .incrementRequestPendingGauge(duplexFlowContext.getServiceName());
    }

    public static void fireEntryOutputComplete(DuplexFlowContext duplexFlowContext, String message) {
        if (!duplexFlowContext.close()) {
            return;
        }
        int targetCode = duplexFlowContext.getTargetStatus();
        duplexFlowContext.setInnerStatus(targetCode);
        duplexFlowContext.getHttpServerRequest().response().setStatusCode(targetCode).end(message);
        complete(duplexFlowContext);
    }

    public static void fireInterruptRequest(DuplexFlowContext duplexFlowContext, StateEnum state, String message) {
        if (!duplexFlowContext.close()) {
            return;
        }
        int statusCode = state.getHttpCode();
        duplexFlowContext.setInnerStatus(statusCode);
        duplexFlowContext.getHttpServerRequest().response().setStatusCode(statusCode).end(message);
        complete(duplexFlowContext);
    }

    private static void complete(DuplexFlowContext duplexFlowContext) {
        Container container = duplexFlowContext.getContainer();
        container.removeDuplexFlowContext(duplexFlowContext.getUuid());
        container.getMetricsManager()
                .decrementRequestPendingGauge(duplexFlowContext.getServiceName());
        container.getMetricsManager()
                .incrementRequestCounter(duplexFlowContext.getServiceName(),
                        String.valueOf(duplexFlowContext.getInnerStatus()),
                        String.valueOf(duplexFlowContext.getTargetStatus()));
        long totalCost = System.currentTimeMillis() - duplexFlowContext.getRequestStartTime();
        container.getMetricsManager()
                .changeRequestLatencyHistogram(duplexFlowContext.getServiceName(), totalCost);
        //todo router log
    }
}
