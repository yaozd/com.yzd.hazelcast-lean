package com.yzd.context;

import com.yzd.internal.Container;
import io.vertx.core.http.HttpServerRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author: yaozh
 * @Description:
 */
@Getter
public class DuplexFlowContext implements FlowContext {
    public static final int UNKNOWN_STATUS = -1;
    public static final String DEFAULT_UUID = "-";
    private static final String UUID_KEY = "uuid";
    private static final String SERVICE_NAME_KEY = "service";
    private static final String NOT_FOUND_SERVICE = "not_found_service";
    private final Container container;
    private final HttpServerRequest httpServerRequest;
    private final long requestStartTime;
    private final AtomicBoolean closed;
    private final String serviceName;
    private final long payload;
    private final String uuid;
    private final boolean valid;
    @Setter
    private int innerStatus;
    @Setter
    private int targetStatus;

    public DuplexFlowContext(Container container,
                             HttpServerRequest httpServerRequest) {
        this.container = container;
        this.httpServerRequest = httpServerRequest;
        this.requestStartTime = System.currentTimeMillis();
        this.uuid = findUUID();
        this.serviceName = findServiceName();
        this.payload = this.httpServerRequest.bytesRead();
        this.closed = new AtomicBoolean(false);
        this.innerStatus = UNKNOWN_STATUS;
        this.targetStatus = UNKNOWN_STATUS;
        this.valid = !DEFAULT_UUID.equals(this.uuid);
    }

    private String findUUID() {
        String param = httpServerRequest.getParam(UUID_KEY);
        return param == null ? DEFAULT_UUID : param;
    }

    private String findServiceName() {
        String param = httpServerRequest.getParam(SERVICE_NAME_KEY);
        return param == null ? NOT_FOUND_SERVICE : param;
    }

    @Override
    public boolean close() {
        return this.closed.compareAndSet(false, true);
    }

    @Override
    public boolean checkTimeout() {
        return System.currentTimeMillis() - requestStartTime >
                container.getProtocolConfig().getMaxRequestTimeout();
    }

    @Override
    public boolean isClosed() {
        return this.closed.get();
    }
}
