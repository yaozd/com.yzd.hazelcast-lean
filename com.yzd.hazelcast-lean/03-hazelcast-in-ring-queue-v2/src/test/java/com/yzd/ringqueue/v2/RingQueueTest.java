package com.yzd.ringqueue.v2;

import org.junit.After;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * @Author: yaozh
 * @Description:
 */
public class RingQueueTest {

    @Test
    public void newRingQueueTest() throws InterruptedException {
        RingQueue rq = new RingQueue();
        rq.start();
        long begin = System.currentTimeMillis(); //测试起始时间
        for (int i = 0; i < 10000000; i++) {
            rq.newTask(10, null).put();
        }
        long end = System.currentTimeMillis(); //测试结束时间
        System.out.println("[use time]:" + (end - begin) + "ms"); //打印使用时间
        //
        Thread.sleep(20000);
        int count = 0;
        for (int i = 0; i < rq.getSlot().length; i++) {
            StepSlot stepSlot = rq.getSlot()[i];
            int size = stepSlot.getTasks().size();
            if (size > 0) {
                System.out.println("stepSlot index:" + i);
                count += size;
            }
        }
        if (count > 0) {
            System.out.println("程序有BUG，未释放Task total:" + count);
        } else {
            System.out.println("执行成功, Total:" + count);
        }
        rq.shutdown();
    }

    @After
    public void end() throws InterruptedException {
        System.out.println("End!");
        Thread.currentThread().join();
    }

    private AtomicLong taskId = new AtomicLong(0);

    @Test
    public void atomicLongTest() {
        methodExecuteTime(o -> {
            for (int i = 0; i < 10000000; i++) {
                //54ms
                taskId.incrementAndGet();
            }
        });
        System.exit(0);
    }

    public void methodExecuteTime(Consumer function) {
        long begin = System.currentTimeMillis(); //测试起始时间
        function.accept(null);
        long end = System.currentTimeMillis(); //测试结束时间
        System.out.println("[use time]:" + (end - begin) + "ms"); //打印使用时间
    }
}