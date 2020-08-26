package com.yzd.queue;

import org.jctools.maps.NonBlockingHashMap;
import org.jctools.maps.NonBlockingHashMapLong;
import org.junit.After;
import org.junit.Test;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @Author: yaozh
 * @Description:
 */
public class MapTests {
    Map<Long, String> tasksConcurrentHashMap = new ConcurrentHashMap<>();
    Map<Long, String> tasksNonBlockingHashMapLong = new NonBlockingHashMapLong<>();
    NonBlockingHashMap<Integer, String> tasksNonBlockingHashMap = new NonBlockingHashMap<>();

    /**
     * 测试结果推荐使用：java 原生：ConcurrentHashMap
     */
    @Test
    public void concurrentHashMapTest() {
        long begin = System.currentTimeMillis(); //测试起始时间
        for (long i = 0; i < 10000000; i++) {
            //4605ms 最优结果
            tasksConcurrentHashMap.put(i, "");
            //12699ms
            //tasksNonBlockingHashMapLong.put(i,"");
            //29311ms
            //tasksNonBlockingHashMap.put(i,"");
        }
        long end = System.currentTimeMillis(); //测试结束时间
        System.out.println("[use time]:" + (end - begin) + "ms"); //打印使用时间
    }

    /**
     * 多线程测试场景
     * 推荐使用JAVA 原生:ConcurrentHashMap
     */
    @Test
    public void multiThreadsTest() {
        long begin = System.currentTimeMillis(); //测试起始时间
        int nThreads = 100;
        CompletableFuture[] tasks = new CompletableFuture[nThreads];
        for (int i = 0; i < nThreads; i++) {
            tasks[i] = CompletableFuture.runAsync(this::call, Executors.newCachedThreadPool());
        }
        CompletableFuture<Void> all = CompletableFuture.allOf(tasks);
        //等待所有异步程序处理完成
        all.join();
        System.out.println(tasksConcurrentHashMap.size()+"  |   "+tasksNonBlockingHashMapLong.size());
        long end = System.currentTimeMillis(); //测试结束时间
        System.out.println("[use time]:" + (end - begin) + "ms"); //打印使用时间
    }
    private AtomicLong n=new AtomicLong(0);
    public void call() {
        long step=n.incrementAndGet();
        long n=100000L;
        for (long i = n*step; i < n*(step+1); i++) {
            //4179ms 最优结果
            tasksConcurrentHashMap.put(i, "");
            //4293ms
            //tasksNonBlockingHashMapLong.put(i,"");
        }
    }

    @After
    public void end() throws InterruptedException {
        System.out.println("End!");
        Thread.currentThread().join();
    }
}
