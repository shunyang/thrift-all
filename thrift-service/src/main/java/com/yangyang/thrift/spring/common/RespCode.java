package com.yangyang.thrift.spring.common;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chenshunyang on 16/8/29.
 */
public enum RespCode {

    // 通用错误
    SERVER_ERROR(500, 5000, "bad server"),


    // PS: 不要使用401错误码，因为IE10的BUG导致IE浏览器下午饭正确识别401状态码
    REQUEST_METHOD_NOT_SUPPORT(405, 4001, "request method not supported"),
    REQUEST_API_NOT_FOUND(404, 4002, "request api not found"),
    HTTP_MEDIA_TYPE_NOT_SUPPORT(406, 4003, "http media type not supported"),
    PARAMETER_ERROR(400, 4004, "parameter invalid"),
    AUTHORIZATION_FAIL(403, 4005, "authorization fail"),
    TOKEN_EXPIRATION(403, 4006, "token expiration"),
    FORBIDDEN_ERROR(403, 4007, "access forbidden"),
    IP_NOT_PERMISSION(403, 4008, "the ip can't access this resource"),
    TOKEN_INVALID_ERROR(403, 4009, "token invalid"),

    // 业务错误
    DATA_NOT_FOUND(404, 4102, "data not found");

    private int httpCode;
    private int code;
    private String message;

    RespCode(int httpCode, int code, String message) {
        this.httpCode = httpCode;
        this.code = code;
        this.message = message;
    }

    public int getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(int httpCode) {
        this.httpCode = httpCode;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String toJson() {
        Map<String,Object> result = new HashMap<String, Object>();
        result.put("code",code);
        result.put("message",message);
        return JSON.toJSONString(result);
    }

    @Override
    public String toString() {
        return String.format("[httpCode:%s, code:%s, message:%s]", httpCode, code, message);
    }
}

