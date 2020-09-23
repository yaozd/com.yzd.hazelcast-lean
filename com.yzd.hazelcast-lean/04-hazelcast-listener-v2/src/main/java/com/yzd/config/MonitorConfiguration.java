package com.yzd.config;

import com.yzd.monitor.MonitorInitException;
import com.yzd.monitor.MonitorThreadFactory;
import com.yzd.monitor.PrometheusMetricsManager;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author yaozh
 */
@Slf4j
@EnableConfigurationProperties(MetricsConfig.class)
@Configuration
public class MonitorConfiguration {

    @Autowired
    private MetricsConfig metricsConfig;

    private ExecutorService executorService;

    @Getter
    private PrometheusMetricsManager metricsManager;

    @PostConstruct
    public void initMonitor() {
        try {
            executorService = new ThreadPoolExecutor(metricsConfig.getExecutorInitSize(),
                    metricsConfig.getExecutorMaxSize(), 0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<>(), new MonitorThreadFactory());
            metricsManager = new PrometheusMetricsManager(metricsConfig, executorService);
        } catch (Exception e) {
            log.error("Start monitor server failed! bind to port [{}].", metricsConfig.getPort(), e);
            throw new MonitorInitException(e);
        }
    }

    @PreDestroy
    public void destroyMonitor() {
        if (metricsManager != null) {
            metricsManager.shutdown();
        }
        if (executorService != null) {
            executorService.shutdownNow();
        }
        log.info("Shutdown monitor success!");
    }

}
