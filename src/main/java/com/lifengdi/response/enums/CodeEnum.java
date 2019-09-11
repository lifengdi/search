package com.lifengdi.response.enums;

import lombok.Getter;

/**
 * code枚举
 * @author 李锋镝
 * @date Create at 13:52 2019/9/5
 */
@Getter
public enum CodeEnum {

    SUCCESS("200", "成功")
    , BAD_REQUEST_CODE("400", "请求错误")
    , SYSTEM_ERROR_CODE("500", "操作失败")
    ;

    private String code;

    private String message;

    CodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
