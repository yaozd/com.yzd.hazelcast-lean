package com.yzd.hazelcast.discovery.strategy;

import com.hazelcast.config.ClasspathXmlConfig;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.junit.Test;

import static org.junit.Assert.*;

public class ConsulDiscoveryStrategyTest {

    @Test
    public void start() throws InterruptedException {
        Config conf =new ClasspathXmlConfig("hazelcast-consul-discovery-spi-example.xml");
        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance(conf);
        Thread.currentThread().join();
    }

    @Test
    public void destroy() {
    }
}