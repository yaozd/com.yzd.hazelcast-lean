package com.yzd.queue;

import lombok.extern.slf4j.Slf4j;

import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * 秒级轮询
 *
 * @Author: yaozh
 * @Description:
 */
public class Steper implements Runnable {
    private AbstractRingQueue rq;
    private ThreadGroup stepGroup = new ThreadGroup("stepGroup");

    public Steper(AbstractRingQueue rq) {
        this.rq = rq;
    }

    private ExecutorService stepPool = Executors.newCachedThreadPool(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            return new Thread(stepGroup, r);
        }
    });

    @Override
    public void run() {
        while (true) {
            try {
                int second = Calendar.getInstance().get(Calendar.MINUTE) * 60 + Calendar.getInstance().get(Calendar.SECOND);
                //获得对应slot
                StepSlot slot = rq.nextStep(second);
                System.out.println("steper 执行了" + second + "|slot大小" + slot.getTasks().size());
                final Map<Long, Task> tasks = slot.getTasks();
                stepPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        Iterator<Map.Entry<Long, Task>> it = tasks.entrySet().iterator();
                        while (it.hasNext()) {
                            Map.Entry<Long, Task> next = it.next();
                            Task task = next.getValue();
                            if (task.getCycle() <= 0) {
                                //System.out.println("Timeout task id:" + next.getValue().getId());
                                it.remove();
                            } else {
                                task.countDown();
                            }
                        }
                    }
                });
                slot.setTasks(new ConcurrentHashMap<>());
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
