package com.yzd.queue;

import org.jctools.queues.SpscArrayQueue;
import org.junit.Test;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @Author: yaozh
 * @Description:
 */
public class LinkedTests {
    private ConcurrentLinkedQueue<Long> tasksConcurrentLinkedQueue = new ConcurrentLinkedQueue<>();
    private Queue<Long> tasksSpscArrayQueue = new SpscArrayQueue(10000000);

    /**
     * SpscArrayQueue插入速度快，但不支持remove操作
     */
    @Test
    public void linkedTest() {
        long begin = System.currentTimeMillis(); //测试起始时间
        for (long i = 0; i < 10000000; i++) {
            //6044ms
            //tasksConcurrentLinkedQueue.add(i);
            //2225ms 最优结果
            tasksSpscArrayQueue.add(i);
            //java.lang.UnsupportedOperationException: remove
            //tasksSpscArrayQueue.remove(i);
        }
        long end = System.currentTimeMillis(); //测试结束时间
        System.out.println("[use time]:" + (end - begin) + "ms"); //打印使用时间
        while (!tasksSpscArrayQueue.isEmpty()){
            tasksSpscArrayQueue.poll();
            int size = tasksSpscArrayQueue.size();
            System.out.println(size);
        }
        System.out.println("End");
    }
}
