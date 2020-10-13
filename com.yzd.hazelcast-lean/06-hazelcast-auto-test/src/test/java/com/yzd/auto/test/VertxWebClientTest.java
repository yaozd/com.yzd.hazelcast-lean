package com.yzd.auto.test;

import org.databene.contiperf.PerfTest;
import org.databene.contiperf.junit.ContiPerfRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.net.URI;
import java.util.UUID;

/**
 * @Author: yaozh
 * @Description:
 */
public class VertxWebClientTest {
    private static final int MAX_POOL_SIZE = 5_000;
    private VertxWebClient vertxWebClient;
    private String urlTemplate;
    @Rule
    public ContiPerfRule i = new ContiPerfRule();

    @Before
    public void init() {
        vertxWebClient = new VertxWebClient(MAX_POOL_SIZE);
        //urlTemplate = "http://127.0.0.1:1000/?uuid=%s";
        urlTemplate = "http://localhost:18081/user/newTask";
        System.out.println("sample:" + newURI());
    }

    @After
    public void end() throws InterruptedException {
        Thread.currentThread().join();
    }

    @Test
    @PerfTest(threads = 10, invocations = 100_000)
    public void get() {
        URI uri = newURI();
        System.out.println(uri);
        vertxWebClient.get(uri);
    }

    private URI newURI() {
        return URI.create(String.format(urlTemplate, UUID.randomUUID().toString()));
    }

}