package com.yzd;

import com.yzd.sender.SenderServer;

/**
 * 双向流方式进行数据推荐
 *
 * @Author: yaozh
 * @Description:
 */

public class StartUpGrpcServer {

    public static void main(String[] args) throws InterruptedException {
        SenderServer server = new SenderServer();
        server.init();
        Thread.currentThread().join();
    }
}
