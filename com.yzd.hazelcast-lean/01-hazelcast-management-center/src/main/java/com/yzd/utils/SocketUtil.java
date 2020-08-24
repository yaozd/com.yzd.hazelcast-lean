package com.yzd.utils;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * @Author: yaozh
 * @Description:
 */
public class SocketUtil {
    /**
     * 随机获取一个可用的端口号
     */
    public static int getAvailablePort() {
        int port = 0;
        ServerSocket s = null;
        try {
            s = new ServerSocket(0);
            port = s.getLocalPort();
            System.out.println("listening on port: " + port);
            s.close();
        } catch (IOException e) {

        }
        return port;
    }
}
