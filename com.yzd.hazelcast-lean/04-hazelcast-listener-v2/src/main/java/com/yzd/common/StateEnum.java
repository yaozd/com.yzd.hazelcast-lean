package com.yzd.common;

/**
 * @author yaozh
 */
public enum StateEnum {


    /**
     * UUID not found
     */
    UUID_NOT_FOUND(403, "UUID not found!"),

    /**
     * Client has closed connection!
     */
    CLIENT_CLOSED_CONNECTION(499, "Client has closed connection!"),
    /**
     * Inner occur error
     */
    INNER_ERROR(502, "Inner error!"),
    /**
     * Business handler error!
     */
    BUSINESS_HANDLER_ERROR(502, "Business handler error!"),
    /**
     * Request handler error!
     */
    REQUEST_HANDLER_ERROR(502, "Request handler error!"),
    /**
     * Request timeout
     */
    REQUEST_TIMEOUT(408, "Request timeout!");


    private int httpCode;

    private String message;

    StateEnum(int httpCode, String message) {
        this.httpCode = httpCode;
        this.message = message;
    }

    public int getHttpCode() {
        return httpCode;
    }

    public String getMessage() {
        return message;
    }

}
