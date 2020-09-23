package com.yzd.config;


import com.yzd.hazelcast.HazelcastSessionStorage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @Author: yaozh
 * @Description:
 */
@Slf4j
@EnableConfigurationProperties(SessionConfig.class)
@Configuration
public class SessionConfiguration {

    @Autowired
    SessionConfig sessionConfig;

    @Getter
    HazelcastSessionStorage hazelcastSessionStorage;

    @PostConstruct
    public void initSession() {
        hazelcastSessionStorage = new HazelcastSessionStorage(sessionConfig);
    }
}
