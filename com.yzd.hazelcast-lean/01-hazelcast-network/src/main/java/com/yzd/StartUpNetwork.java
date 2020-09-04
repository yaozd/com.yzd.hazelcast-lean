package com.yzd;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class StartUpNetwork {
    public static final Boolean writer = Boolean.parseBoolean(System.getProperty("writer", "true"));
    public static final Boolean reader = Boolean.parseBoolean(System.getProperty("reader", "true"));

    public static void main(String[] args) throws InterruptedException {
        MyHazelcast.getInstance().init();
        if (writer) {
            writeValueJob();
        }
        if (reader) {
            readerValueJob();
        }
        checkOtherSetJob();
        Thread.currentThread().join();
    }

    private static void writeValueJob() {
        ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(1);
        executor.schedule((Runnable) () -> {
            System.out.println("W1:" + System.currentTimeMillis());
            while (true) {
                try {
                    MyHazelcast.getInstance().write();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 0, TimeUnit.SECONDS);
    }

    private static void readerValueJob() {
        ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(1);
        executor.schedule((Runnable) () -> {
            int oldSize = 0;
            long oldTime = System.currentTimeMillis();
            System.out.println("R1:" + System.currentTimeMillis());
            while (true) {
                try {
                    int newSize = MyHazelcast.getInstance().size();
                    long newTime = System.currentTimeMillis();
                    if (oldSize != newSize) {
                        System.out.println("New size:" + newSize + "  Time:" + (newTime - oldTime));
                        oldSize = newSize;
                        oldTime = newTime;
                    }
                    Thread.sleep(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 0, TimeUnit.SECONDS);
    }

    private static void checkOtherSetJob() {
        ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(1);
        executor.schedule((Runnable) () -> {
            while (true) {
                try {
                    MyHazelcast.getInstance().checkOtherSet();
                    Thread.sleep(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, 0, TimeUnit.SECONDS);
    }
}
