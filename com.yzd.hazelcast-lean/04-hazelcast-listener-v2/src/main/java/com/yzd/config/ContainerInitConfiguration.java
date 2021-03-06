package com.yzd.config;

import com.yzd.internal.Container;
import com.yzd.internal.ContainerInitException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @Author: yaozh
 * @Description:
 */
@Slf4j
@Configuration
public class ContainerInitConfiguration {

    @Autowired
    private ContainerProperties containerProperties;
    @Autowired
    private MonitorConfiguration monitorConfiguration;

    @PostConstruct
    private void initContainer() {
        log.info("init");
        ContainerConfig containerConfig = containerProperties.getContainer();
        if (containerConfig == null) {
            log.error("Init container error, config not found!");
            throw new ContainerInitException("ContainerInitConfiguration not found");
        }
        Container container = Container.getInstance();
        container.setMetricsManager(monitorConfiguration.getMetricsManager());
        try {
            container.start(containerConfig);
        } catch (Exception ex) {
            log.error("Container start fail!", ex);
            System.exit(1);
        }
    }

    @PreDestroy
    private void destroyContainer() {
        log.info("Shutdown container progressing …………");
        Container.getInstance().shutdown();

    }

}
