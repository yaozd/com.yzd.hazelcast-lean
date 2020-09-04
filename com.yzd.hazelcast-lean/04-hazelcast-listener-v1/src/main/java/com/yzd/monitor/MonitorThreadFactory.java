package com.yzd.monitor;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: yaozh
 * @Description:
 */
public class MonitorThreadFactory implements ThreadFactory {

    public static final String PREFIX = "T-Prometheus-Monitor-";

    private static final AtomicInteger THREAD_COUNT = new AtomicInteger(0);

    @Override
    public Thread newThread(Runnable runnable) {
        return new Thread(runnable, PREFIX + THREAD_COUNT.incrementAndGet());
    }
}
