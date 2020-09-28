package com.yzd.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: yaozh
 * @Description:
 */
@Slf4j
public class SocketUtil {
    private final static AtomicInteger INIT_PORT = new AtomicInteger(1000);

    /**
     * 随机获取一个可用的端口号
     */
    public static int getRandomPort() {
        while (true) {
            int port = INIT_PORT.getAndIncrement();
            ServerSocket s = null;
            try {
                s = new ServerSocket(port);
                port = s.getLocalPort();
                s.close();
            } catch (IOException e) {
                continue;
            }
            log.warn("Random port: " + port);
            return port;
        }
    }
}
