package com.yzd.ringqueue.v2;

/**
 * 任务体
 *
 * @Author: yaozh
 * @Description:
 */
public class Task extends AbstractTask {
    private Object object;
    private AbstractRingQueue ringQueue;

    public Task(long id, int second, int after, Object object, AbstractRingQueue ringQueue) {
        super(id, second, after);
        this.object = object;
        this.ringQueue = ringQueue;
    }

    public void put() {
        this.ringQueue.add(this);
    }
}

