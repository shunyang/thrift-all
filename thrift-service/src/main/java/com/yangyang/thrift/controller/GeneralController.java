package com.yangyang.thrift.controller;

import com.yangyang.thrift.common.RespCode;
import com.yangyang.thrift.util.HttpContextHelper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by chenshunyang on 16/8/29.
 */

@RestController
public class GeneralController {

    @RequestMapping(value = "/404")
    public Object requestApiNotFound() {
        return HttpContextHelper.buildResponse(RespCode.REQUEST_API_NOT_FOUND);
    }
}
