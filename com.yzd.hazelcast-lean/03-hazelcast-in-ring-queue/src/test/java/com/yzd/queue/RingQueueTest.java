package com.yzd.queue;

import org.junit.After;
import org.junit.Test;

/**
 * @Author: yaozh
 * @Description:
 */
public class RingQueueTest {
    @Test
    public void ringQueueTest() throws InterruptedException {
        RingQueue rq = new RingQueue();
        rq.start();
        //当前程序有bug,不管是否在开始的时候，进行sleep操作，程序都会有问题
        Thread.sleep(1000);
        long begin = System.currentTimeMillis(); //测试起始时间
        for (int i = 0; i < 10000000; i++) {
            Task task = new Task(i, 5, null);
            rq.add(task);
        }
        long end = System.currentTimeMillis(); //测试结束时间
        System.out.println("[use time]:" + (end - begin) + "ms"); //打印使用时间
        //Thread.sleep(20000);
        for (int i = 0; i < 10000000; i++) {
            Task task = new Task(i, 5, null);
            rq.add(task);
        }
        Thread.sleep(60000);
        int count = 0;
        for (StepSlot stepSlot : rq.getSlot()) {
            int size = stepSlot.getTasks().size();
            if (size > 0) {
                count += size;
            }
        }
        if (count > 0) {
            System.out.println("程序有BUG，未释放Task total:" + count);
        } else {
            System.out.println("执行成功, Total:" + count);
        }
        System.exit(0);
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