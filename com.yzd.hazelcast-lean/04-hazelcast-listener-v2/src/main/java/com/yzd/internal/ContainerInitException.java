package com.yzd.internal;

/**
 * @Author: yaozh
 * @Description:
 */
public class ContainerInitException extends RuntimeException {

    public ContainerInitException(String message) {
        super(message);
    }

    public ContainerInitException(String message, Throwable cause) {
        super(message, cause);
    }
}
