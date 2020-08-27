package com.yzd.ringqueue.v2;

import lombok.Getter;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 抽象环形队列
 *
 * @Author: yaozh
 * @Description:
 */
public abstract class AbstractRingQueue {
    protected static final int ONE_HOUR = 3600;
    protected static final int ONE_SECOND = 1;
    protected AtomicLong taskId = new AtomicLong(0);
    protected Integer currentSecond;
    @Getter
    protected StepSlot[] slot = new StepSlot[3600];

    public AbstractRingQueue() {
        for (int i = 0; i < ONE_HOUR; i++) {
            this.slot[i] = new StepSlot();
        }
    }

    /**
     * 获取下个插槽
     *
     * @param slotIndex 插槽位置
     * @return
     */
    public abstract StepSlot nextStep(int slotIndex);

    /**
     * 添加任务
     *
     * @param task 任务
     * @return 所在slot的index
     */
    public abstract int add(Task task);

    /**
     * 删除任务
     *
     * @param taskId    任务id
     * @param slotIndex 插槽位置
     */
    public abstract void remove(int slotIndex, long taskId);

    /**
     * 替换task
     *
     * @param slotIndex 槽位置
     * @param task      任务
     */
    public abstract void replaceSlot(int slotIndex, Task task);

    /**
     * 获取任务ID
     *
     * @return
     */
    protected abstract long getTaskId();

    /**
     * 更新时钟
     */
    protected abstract void touch();

    /**
     * 启动
     */
    protected abstract void start();

    /**
     * 关闭
     */
    protected abstract void shutdown();
}
