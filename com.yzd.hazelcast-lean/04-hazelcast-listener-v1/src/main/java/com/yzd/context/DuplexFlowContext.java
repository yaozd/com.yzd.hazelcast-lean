package com.yzd.context;

import com.yzd.internal.Container;
import io.vertx.core.http.HttpServerRequest;
import lombok.Getter;

/**
 * @Author: yaozh
 * @Description:
 */
@Getter
public class DuplexFlowContext implements FlowContext {
    private final Container container;
    private final HttpServerRequest httpServerRequest;
    private final long requestStartTime;
    private boolean closed;


    public DuplexFlowContext(Container container,
                             HttpServerRequest httpServerRequest) {
        this.container = container;
        this.httpServerRequest = httpServerRequest;
        requestStartTime = System.currentTimeMillis();
    }

    @Override
    public void close() {
        this.closed = true;
    }

    @Override
    public boolean checkTimeout() {
        return System.currentTimeMillis() - requestStartTime >
                container.getProtocolConfig().getMaxRequestTimeout();
    }
}
