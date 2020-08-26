package com.yzd.queue;

/**
 * 任务体
 *
 * @Author: yaozh
 * @Description:
 */
public class Task extends AbstractTask {
    private Object object;

    public Task(long id, int after, Object object) {
        super(id, after);
        this.object = object;
    }
}

