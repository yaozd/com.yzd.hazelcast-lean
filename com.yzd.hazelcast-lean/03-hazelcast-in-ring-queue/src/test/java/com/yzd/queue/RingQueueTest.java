package com.yzd.queue;

import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @Author: yaozh
 * @Description:
 */
public class RingQueueTest {
    @Test
    public void ringQueueTest() throws InterruptedException {
        RingQueue rq = new RingQueue();
        rq.start();
        long begin = System.currentTimeMillis(); //测试起始时间
        for (int i = 0; i < 10000000; i++) {
            Task task=new Task(i,10,null);
            rq.add(task);
        }
        long end = System.currentTimeMillis(); //测试结束时间
        System.out.println("[use time]:" + (end - begin) + "ms"); //打印使用时间
        //Thread.sleep(20000);
        for (int i = 0; i < 10000000; i++) {
            Task task=new Task(i,10,null);
            rq.add(task);
        }
    }

    @Test
    public void nextStep() {
    }

    @Test
    public void add() {
    }

    @Test
    public void remove() {
    }

    @Test
    public void replaceSlot() {
    }
    @After
    public void end() throws InterruptedException {
        System.out.println("End!");
        Thread.currentThread().join();
    }
}