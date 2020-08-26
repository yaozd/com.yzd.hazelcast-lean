package com.yzd.queue;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: yaozh
 * @Description:
 */

public class StepSlot {
    @Getter
    @Setter
    private volatile Map<Long, Task> tasks = new ConcurrentHashMap<>();

    /**
     * 向槽内添加任务
     *
     * @param task 任务
     */
    int addTask(Task task) {
        tasks.put(task.getId(), task);
        return task.getIndex();
    }

    /**
     * 删除槽内某个taskId的任务
     *
     * @param taskId 任务id
     * @return 成功/失败
     */
    void remove(Long taskId) {
        tasks.remove(taskId);
    }
}
