package com.yzd.auto.test;

import org.databene.contiperf.PerfTest;
import org.databene.contiperf.junit.ContiPerfRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

/**
 * @Author: yaozh
 * @Description:
 */
public class VertxWebClientTest {
    private static final int MAX_POOL_SIZE = 1000;
    private VertxWebClient vertxWebClient;
    private String urlTemplate;
    @Rule
    public ContiPerfRule i = new ContiPerfRule();

    @Before
    public void init() {
        vertxWebClient = new VertxWebClient(MAX_POOL_SIZE);
        urlTemplate = "http://127.0.0.1:1000/?uuid=%s";
        System.out.println(newURI());
    }

    @After
    public void end() throws InterruptedException {
        Thread.currentThread().join();
    }

    @Test
    @PerfTest(threads = 100, invocations = 100000)
    public void get() {
        URI uri = newURI();
        vertxWebClient.get(uri);
    }

    private URI newURI() {
        try {
            return new URI(String.format(urlTemplate, UUID.randomUUID().toString()));
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}