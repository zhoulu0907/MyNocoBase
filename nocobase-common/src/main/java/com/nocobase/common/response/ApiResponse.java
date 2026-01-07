package com.nocobase.common.response;

import lombok.Data;

import java.io.Serializable;

/**
 * Unified API Response
 * @param <T> Response data type
 */
@Data
public class ApiResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Response code (200 for success, others for errors)
     */
    private Integer code;

    /**
     * Response message
     */
    private String message;

    /**
     * Response data
     */
    private T data;

    /**
     * Timestamp
     */
    private Long timestamp;

    public ApiResponse() {
        this.timestamp = System.currentTimeMillis();
    }

    public ApiResponse(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Success response with data
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "Success", data);
    }

    /**
     * Success response without data
     */
    public static <T> ApiResponse<T> success() {
        return success(null);
    }

    /**
     * Success response with custom message
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, message, data);
    }

    /**
     * Error response
     */
    public static <T> ApiResponse<T> error(Integer code, String message) {
        return new ApiResponse<>(code, message, null);
    }

    /**
     * Error response with default code 500
     */
    public static <T> ApiResponse<T> error(String message) {
        return error(500, message);
    }

    /**
     * Bad request response (400)
     */
    public static <T> ApiResponse<T> badRequest(String message) {
        return error(400, message);
    }

    /**
     * Not found response (404)
     */
    public static <T> ApiResponse<T> notFound(String message) {
        return error(404, message);
    }
}
