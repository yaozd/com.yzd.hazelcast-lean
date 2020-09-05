package com.yzd.discovery.consul;

import com.hazelcast.config.ClasspathXmlConfig;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.junit.Test;


public class ConsulDiscoveryStrategyFactoryTest {
    @Test
    public void consulDiscoveryTest() throws InterruptedException {
        System.out.println("Consul discovery test starting!");
        Config conf =new ClasspathXmlConfig("hazelcast-consul-discovery-spi-example.xml");
        HazelcastInstance hazelcastInstance = Hazelcast.newHazelcastInstance(conf);
        Thread.sleep(100000000);
        Thread.currentThread().join();
    }
}