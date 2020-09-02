package com.yzd.monitor;


import com.yzd.config.MetricsConfig;

import java.util.concurrent.ExecutorService;

/**
 * @author yaozh
 */
public interface MetricsManager {

    /**
     * Init metrics manager
     */
    void init(MetricsConfig metricsConfig, ExecutorService executorService) throws Exception;

    /**
     * Shutdown metrics manager and release resource
     */
    void shutdown();

    /**
     * Increment connection gauge
     */
    void incrementConnectionGauge();

    /**
     * Decrement connection gauge
     */
    void decrementConnectionGauge();

    /**
     * Increment exception counter
     *
     * @param serviceName
     */
    void incrementException(String serviceName);

    /**
     * Increment request pending gauge
     *
     * @param serviceName
     */
    void incrementRequestPendingGauge(String serviceName);

    /**
     * Decrement request pending gauge
     *
     * @param serviceName
     */
    void decrementRequestPendingGauge(String serviceName);

    /**
     * Increment request counter
     *
     * @param serviceName
     */
    void incrementRequestCounter(String serviceName, String innerStatus, String targetStatus);

    /**
     * Change request latency histogram
     *
     * @param serviceName
     * @param latency
     */
    void changeRequestLatencyHistogram(String serviceName, long latency);

    /**
     * Increment payload counter
     * 目前Vert.x流量暂时统计不到
     *
     * @param serviceName
     * @param amount
     */
    void incrementPayloadCounter(String serviceName, long amount);

    /**
     * Add the commit ID of GIT code to Prometheus, so that you can know the version of the currently running code
     *
     * @param shortCommitId
     */
    void gitShortCommitIdGauge(String shortCommitId);

    /**
     * hyperspace current running container config version
     *
     * @param version
     */
    void containerConfigVersionGauge(int version);

    /**
     * Active request  context
     * 活跃请求上下文
     *
     * @param amount
     */
    void activeRequestContextGauge(int amount);

}
