package com.yzd.ringqueue.v2;

/**
 * @Author: yaozh
 * @Description:
 */
public abstract class AbstractTask {
    //圈数
    private int cycle;

    private int index;

    private long id;


    public AbstractTask(long id, int second, int after) {
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