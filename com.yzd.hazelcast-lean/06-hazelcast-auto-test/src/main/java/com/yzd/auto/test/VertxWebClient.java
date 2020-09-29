package com.yzd.auto.test;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;

/**
 * @Author: yaozh
 * @Description:
 */
public class VertxWebClient {
    private final static String QUERY_SPLIT_CHART = "&";
    private final static String PARAM_SPLIT_CHART = "=";
    private final WebClient client;

    public VertxWebClient(int maxPoolSize) {
        WebClientOptions options = new WebClientOptions()
                .setUserAgent("My-App/1.2.3");
        options.setKeepAlive(false);
        //default value:5
        options.setMaxPoolSize(maxPoolSize);
        client = WebClient.create(Vertx.vertx(), options);
    }

    public void get(URI uri) {
        String[] queryList = StringUtils.split(uri.getRawQuery(), QUERY_SPLIT_CHART);
        HttpRequest<Buffer> httpRequest = client.get(uri.getPort(), uri.getHost(), uri.getRawPath());
        for (int i = 0; i < queryList.length; i++) {
            String[] params = StringUtils.split(queryList[i], PARAM_SPLIT_CHART);
            httpRequest.addQueryParam(params[0], params[1]);
        }
        httpRequest.send(ar -> {
            if (ar.succeeded()) {
                // Obtain response
                HttpResponse<Buffer> response = ar.result();
                System.out.println("Received response with status code: " + response.statusCode());
            } else {
                System.out.println("Something went wrong " + ar.cause().getMessage());
            }
        });
    }
}
