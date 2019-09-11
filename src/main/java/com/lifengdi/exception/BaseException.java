package com.lifengdi.exception;

import com.lifengdi.exception.factory.ApiExceptionFactory;
import com.lifengdi.global.Global;
import lombok.Getter;

/**
 * 基础异常
 * @author 李锋镝
 * @date Create at 17:11 2018/7/5
 * @modified by
 */
@Getter
public enum BaseException implements ApiExceptionFactory {

    OPERATE_ERROR("1000", "操作失败")
    ,NULL_USER_EXCEPTION("1001", "用户数据为空")

    , NULL_DATA_EXCEPTION("1010", "数据为空")
    , GET_ENTITY_ERROR("1011", "数据不存在")
    , SQL_ERROR("1012", "数据异常")
    , QUERY_EXCEPTION("1013", "查询失败")

    , RESUBMIT_EXCEPTION("1020", "请勿重复提交")
    , NULL_PARAM_EXCEPTION("1021", "参数为空")
    , ERROR_PARAM_EXCEPTION("1022", "参数错误")
    , GET_REQUEST_EXCEPTION("1023", "GET请求出现异常")
    , POST_REQUEST_EXCEPTION("1024", "POST请求出现异常")

    , URL_FORMAT_ERROR("1030", "URL格式不正确")
    , DAYS_INPUT_FORMAT_EXCEPTION("1031", "输入格式不正确")

    , INDEX_EXCEPTION("1040", "更新索引失败")
    , INDEX_NOT_EXISTS_EXCEPTION("1041", "更新索引失败，当前索引不存在")
    , BULK_UPDATE_INDEX_EXCEPTION("1042", "批量更新索引失败")
    ;

    @Override
    public String prefix() {
        return "BASE_";
    }

    private String code;

    private String message;

    BaseException(String code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 构建异常
     * @return ApiException
     */
    public ApiException build() {
        return apply(code, message);
    }

    /**
     * 构建异常，拼接自定义错误信息
     * @param msg 自定义信息
     * @return ApiException
     */
    public ApiException build(String msg) {
        return apply(code, message + Global.SPLIT_FLAG_COMMA + msg);
    }

    /**
     * 构建异常，自定义多个错误信息
     * @param msg 自定义错误信息
     * @return ApiException
     */
    public ApiException builds(String ...msg) {
        return apply(code, String.format(message, msg));
    }

}

