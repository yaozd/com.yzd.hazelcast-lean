package com.yzd.queue;

/**
 * 环形队列
 *
 * @Author: yaozh
 * @Description:
 */
public class RingQueue extends AbstractRingQueue {
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

    public void start() {
        new Thread(new Steper(this), "Thread-RingQueue").start();
    }
}
