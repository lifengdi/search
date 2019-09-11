package com.lifengdi.exception;

/**
 * @author 李锋镝
 * @date Create at 13:39 2019/9/5
 */
public class ApiException extends RuntimeException {

    private static final long serialVersionUID = -554195039656665546L;
    private String code;
    private String message;

    public ApiException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String toString() {
        return "ApiException(code=" + this.getCode() + ", message=" + this.getMessage() + ")";
    }
}
