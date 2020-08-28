package com.yzd.context;

/**
 * @Author: yaozh
 * @Description:
 */
public interface FlowContext {
    /**
     * Close
     */
    void close();

    /**
     * Check timeout
     * @return
     */
    boolean checkTimeout();

    /**
     * Is closed
     * @return
     */
    boolean isClosed();
}
