package com.yangyang.thrift.service.impl;


import com.yangyang.thrift.service.HealthService;
import org.apache.thrift.TException;
import org.springframework.stereotype.Service;


@Service("healthServiceImpl")
public class HealthServiceImpl implements HealthService.Iface {
    @Override
    public String ping() throws TException {
        return "pong";
    }
}
