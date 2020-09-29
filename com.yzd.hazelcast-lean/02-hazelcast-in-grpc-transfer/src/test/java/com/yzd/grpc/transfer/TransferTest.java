package com.yzd.grpc.transfer;

import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @Author: yaozh
 * @Description:
 */
public class TransferTest {

    @After
    public void end() throws InterruptedException {
        Thread.currentThread().join();
    }
    @Test
    public void init() {
        Transfer transfer=new Transfer(30200);
        transfer.init();
    }
}