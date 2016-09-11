package com.yangyang.thrift.util;

import com.alibaba.fastjson.JSON;
import com.yangyang.thrift.spring.common.RespCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
/**
 * Created by chenshunyang on 16/8/29.
 */
public class HttpContextHelper {

    private static Logger Log = LoggerFactory.getLogger(HttpContextHelper.class);

    public static ResponseEntity<?> buildResponse(int httpCode, int code, String content) {
        Map<String, Object> body = new HashMap<>();
        body.put("code", code);
        body.put("content", content);
        return buildResponse(httpCode, body);
    }

    public static ResponseEntity<?> buildResponse(int httpCode, Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/json; charset=UTF-8"));
        Log.info("Response: httpCode:{}, body:{}", httpCode, JSON.toJSONString(body));
        return new ResponseEntity<>(body, headers, HttpStatus.valueOf(httpCode));
    }

    public static ResponseEntity<?> buildResponse(int httpCode) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/json; charset=UTF-8"));
        Log.info("Response: httpCode:{}", httpCode);
        return new ResponseEntity<>(headers, HttpStatus.valueOf(httpCode));
    }

    public static ResponseEntity<?> buildResponse(RespCode errorCode) {
        return buildResponse(errorCode.getHttpCode(), errorCode.getCode(), errorCode.getMessage());
    }

    public static void response(HttpServletResponse response, RespCode errorCode) throws IOException {
        response.setContentType("application/json");
        response.setStatus(errorCode.getHttpCode());
        response.getWriter().print(errorCode.toJson());
    }

    public static String getRemoteIp(HttpServletRequest request) {
        String ipAddress = null;
        //ipAddress = request.getRemoteAddr();
        ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("X-Real-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if (ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")) {
                //根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    Log.debug("remote ip get failed !");
                }
                ipAddress = inet.getHostAddress();
            }
        }

        //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ipAddress != null && ipAddress.length() > 15) { //"***.***.***.***".length() = 15
            if (ipAddress.indexOf(",") > 0) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
            }
        }
        return ipAddress;
    }
}
