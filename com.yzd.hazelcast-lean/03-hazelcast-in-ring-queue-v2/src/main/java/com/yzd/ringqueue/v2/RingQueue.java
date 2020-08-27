package com.yzd.ringqueue.v2;

import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 环形队列
 *
 * @Author: yaozh
 * @Description:
 */
public class RingQueue extends AbstractRingQueue implements Runnable {

    private ScheduledExecutorService executorService;
    private ExecutorService stepPool;

    public RingQueue() {
        super();

    }

    @Override
    public StepSlot nextStep(int slotIndex) {
        return slot[slotIndex];
    }

    @Override
    public int add(Task task) {
        return slot[task.getIndex()].addTask(task);
    }

    @Override
    public void remove(int slotIndex, long taskId) {
        slot[slotIndex].remove(taskId);
    }

    @Override
    public void replaceSlot(int slotIndex, Task task) {
        remove(slotIndex, task.getId());
        add(task);
    }

    @Override
    protected long getTaskId() {
        return this.taskId.incrementAndGet();
    }

    @Override
    protected void touch() {
        this.currentSecond = (this.currentSecond + 1) % ONE_HOUR;
    }

    public Task newTask(int after, Object object) {
        if (this.currentSecond == null) {
            throw new IllegalStateException("ring queue not start!");
        }
        return new Task(this.getTaskId(), this.currentSecond, after, object, this);
    }

    @Override
    public void start() {
        if (this.currentSecond != null) {
            return;
        }
        this.currentSecond = Calendar.getInstance().get(Calendar.MINUTE) * 60
                + Calendar.getInstance().get(Calendar.SECOND);
        this.stepPool = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                5L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(),
                new ThreadFactory() {
                    AtomicInteger threadNum = new AtomicInteger(0);

                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "T-RingQueue-Step-" + threadNum.getAndIncrement());
                    }
                });
        this.executorService = new ScheduledThreadPoolExecutor(1, r -> {
            Thread thread = new Thread(r, "T-RingQueue");
            thread.setDaemon(true);
            return thread;
        });
        executorService.scheduleAtFixedRate(this::run, ONE_SECOND, ONE_SECOND, TimeUnit.SECONDS);
    }

    @Override
    public void shutdown() {
        if (executorService != null) {
            executorService.shutdownNow();
        }
        stepPool.shutdownNow();
    }

    @Override
    public void run() {
        try {
            int second = this.currentSecond;
            this.touch();
            //获得对应slot
            StepSlot slot = this.nextStep(second);
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
            //创建新的对象，解决旧对象在扩容占用内存过大,FULL GC无法回收对象问题
            slot.setTasks(new ConcurrentHashMap<>());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
