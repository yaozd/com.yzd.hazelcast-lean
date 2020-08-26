package com.yzd.queue;

import java.util.Calendar;

/**
 * @Author: yaozh
 * @Description:
 */
public abstract class AbstractTask {
    //圈数
    private int cycle;

    private int index;

    private long id;


    public AbstractTask(long id, int after) {
        int second = Calendar.getInstance().get(Calendar.MINUTE) * 60 + Calendar.getInstance().get(Calendar.SECOND);
        this.index = (second + after) % 3600;
        this.cycle = after / 3600;
        this.id = id;
    }

    public int getCycle() {
        return this.cycle;
    }

    public void countDown() {
        this.cycle -= 1;
    }

    public int getIndex() {
        return index;
    }

    public long getId() {
        return id;
    }
}