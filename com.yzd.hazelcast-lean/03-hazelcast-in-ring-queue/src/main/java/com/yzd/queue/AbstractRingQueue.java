package com.yzd.queue;

/**
 * 抽象环形队列
 * @Author: yaozh
 * @Description:
 */
public abstract class AbstractRingQueue {

    public static final int ONE_HOUR=3600;
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
     * @param slotIndex 槽位置
     * @param task 任务
     */
    public abstract void replaceSlot(int slotIndex, Task task);

}
