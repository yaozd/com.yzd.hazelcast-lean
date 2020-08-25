package com.yzd.config;

import com.yzd.internal.Container;
import lombok.extern.slf4j.Slf4j;
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
    @PostConstruct
    private void initContainer(){
        log.info("init");
        Container.getInstance().start();

    }
    @PreDestroy
    private void destroyContainer() {
        Container.getInstance().shutdown();
        log.info("shutdown");
    }
}
