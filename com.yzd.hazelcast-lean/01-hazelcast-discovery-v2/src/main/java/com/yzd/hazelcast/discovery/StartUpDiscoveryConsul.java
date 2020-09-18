package com.yzd.hazelcast.discovery;

import com.hazelcast.config.ClasspathXmlConfig;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;

/**
 * 启动程序
 */
public class StartUpDiscoveryConsul {

    public static void main(String[] args) throws InterruptedException {
        Config conf = new ClasspathXmlConfig("hazelcast-consul-discovery-spi-example.xml");
        Hazelcast.newHazelcastInstance(conf);
        Thread.currentThread().join();
    }
}
