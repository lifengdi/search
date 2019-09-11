package com.lifengdi.exception;

import com.lifengdi.response.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;

import static java.util.stream.Collectors.joining;

/**
 * 异常处理类
 */
@Slf4j
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ResponseResult<String> bindExceptionHandler(MethodArgumentNotValidException ex) {
        // 获取校验失败的字段的错误信息
        final String message = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(joining("\n"));
        log.warn("无效请求参数 message={}", message);
        return ResponseResult.badRequestError(message);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ResponseResult<String> bindExceptionHandler(IllegalArgumentException ex) {
        // 获取校验失败的字段的错误信息
        final String message = ex.getMessage();
        log.warn("无效请求参数 message={}", message, ex);
        return ResponseResult.badRequestError(message);
    }

    /**
     * 包装异常信息返回
     *
     * @param exception Exception
     * @return ResponseResult<Object>
     */
    @ExceptionHandler(Throwable.class)
    @ResponseStatus(code = HttpStatus.OK)
    public ResponseResult<String> handleApiException(Throwable exception, HttpServletRequest request) {
        if (exception instanceof ApiException) {
            ApiException e = (ApiException) exception;

            return new ResponseResult<>(e);
        }
        final String url = request.getMethod() + ' ' + request.getRequestURI() + (request.getQueryString() == null ? "" : '?' + request.getQueryString());
        exception.printStackTrace();
        log.error("异常处理器 url:{},message={},exception:{}", url, exception.getMessage(), exception);
        if (exception instanceof SQLException) {
            return ResponseResult.fail(BaseException.SQL_ERROR.build());
        }
        if (StringUtils.isBlank(exception.getMessage())) {
            return ResponseResult.fail(BaseException.OPERATE_ERROR.build());
        }
        return ResponseResult.fail(exception);
    }

}
