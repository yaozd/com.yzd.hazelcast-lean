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

    private static void fireEntryOutputComplete(DuplexFlowContext duplexFlowContext) {
        if (!duplexFlowContext.close()) {
            return;
        }
        routerLog(duplexFlowContext);
    }

    public static void fireInterruptRequest(DuplexFlowContext duplexFlowContext, StateEnum state, String message) {
        if (!duplexFlowContext.close()) {
            return;
        }
        Container container = duplexFlowContext.getContainer();
        container.removeDuplexFlowContext(duplexFlowContext.getUuid());
        int statusCode = state.getHttpCode();
        duplexFlowContext.setInnerStatus(statusCode);
        container.getMetricsManager()
                .decrementRequestPendingGauge(duplexFlowContext.getServiceName());
        container.getMetricsManager()
                .incrementRequestCounter(duplexFlowContext.getServiceName(),
                        String.valueOf(duplexFlowContext.getInnerStatus()),
                        String.valueOf(duplexFlowContext.getTargetStatus()));
        duplexFlowContext.getHttpServerRequest().response().setStatusCode(statusCode).end(message);
        routerLog(duplexFlowContext);
    }

    private static void routerLog(DuplexFlowContext duplexFlowContext) {
        long totalCost = System.currentTimeMillis() - duplexFlowContext.getRequestStartTime();
        duplexFlowContext.getContainer().getMetricsManager()
                .changeRequestLatencyHistogram(duplexFlowContext.getServiceName(), totalCost);
    }
}
