package com.lifengdi.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lifengdi.exception.ApiException;
import com.lifengdi.response.enums.CodeEnum;
import lombok.Data;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS;

@Data
@JsonInclude(ALWAYS)
public class ResponseResult<T> {

    private String code;

    private String message;

    private T data;


    public ResponseResult(ApiException e) {
        this.code = e.getCode();
        this.message = e.getMessage();
    }

    public ResponseResult(T data) {
        this.code = CodeEnum.SUCCESS.getCode();
        this.message = CodeEnum.SUCCESS.getMessage();
        this.data = data;
    }

    public ResponseResult(String errCode, String errMsg, T data) {
        this.code = errCode;
        this.message = errMsg;
        this.data = data;
    }

    public ResponseResult() {}

    public static <T> ResponseResult<T> success(T data) {
        return new ResponseResult<>(data);
    }

    public static <T>ResponseResult<T> badRequestError(String message) {
        return new ResponseResult<>(CodeEnum.BAD_REQUEST_CODE.getCode(), message, null);
    }


    /**
     * 返回异常
     *
     * @param e Exception
     * @return ResponseResult
     */
    public static <T> ResponseResult<T> fail(Throwable e) {
        if (e instanceof ApiException) {
            return new ResponseResult<>((ApiException) e);
        }
        return new ResponseResult<>(CodeEnum.SYSTEM_ERROR_CODE.getCode(), e.getMessage(), null);
    }

    public static ResponseResult fail(ApiException e, Object data) {
        return new ResponseResult<>(e.getCode(), e.getMessage(), data);
    }

}

