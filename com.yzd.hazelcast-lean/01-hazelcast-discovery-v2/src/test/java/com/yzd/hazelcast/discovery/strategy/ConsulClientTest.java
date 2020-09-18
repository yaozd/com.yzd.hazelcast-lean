package com.yzd.hazelcast.discovery.strategy;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ConsulClientTest {

    @Before
    public void init() {
        ConsulClient.getInstance().init(
                "A",
                "B",
                "127.0.0.1",
                80,
                11,
                "BJ"
        );
    }

    @After
    public void end() throws InterruptedException {
        Thread.currentThread().join();
    }

    @Test
    public void register() {
        ConsulClient.getInstance().register();

    }

    @Test
    public void deregister() {
        ConsulClient.getInstance().deregister();
    }
}