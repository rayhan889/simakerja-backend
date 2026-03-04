package com.rynrama.simakerjabackend.util;

public class GlobalAPIResponse<T> {

    private boolean success;
    private T data;
    private String message;
    private String errorCode;
    private String timestamp;

    public GlobalAPIResponse() {
    }

    public GlobalAPIResponse(
            boolean success,
            T data,
            String message
    ) {
        this.success = success;
        this.data = data;
        this.message = message;
        this.timestamp = java.time.OffsetDateTime.now().toString();
    }

    public GlobalAPIResponse(boolean success, T data, String message, String errorCode) {
        this.success = success;
        this.data = data;
        this.message = message;
        this.errorCode = errorCode;
        this.timestamp = java.time.OffsetDateTime.now().toString();
    }


    public static <T> GlobalAPIResponse<T> success(T data) {
        return new GlobalAPIResponse<>(true, data, null);
    }

    public static <T> GlobalAPIResponse<T> success(T data, String message) {
        return new GlobalAPIResponse<>(true, data, message);
    }

    public static <T> GlobalAPIResponse<T> error(String message) {
        return new GlobalAPIResponse<>(false, null, message);
    }

    public static <T> GlobalAPIResponse<T> error(String message,  String errorCode) {
        return new GlobalAPIResponse<>(false, null, message, errorCode);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
