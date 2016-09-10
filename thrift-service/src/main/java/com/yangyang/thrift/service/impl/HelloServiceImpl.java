package com.yangyang.thrift.service.impl;


import com.yangyang.thrift.service.DemoParam;
import com.yangyang.thrift.service.DemoResult;
import com.yangyang.thrift.service.HelloService;
import org.apache.thrift.TException;
import org.springframework.stereotype.Service;


@Service("helloServiceImpl")
public class HelloServiceImpl implements HelloService.Iface {
    @Override
    public String hello(String msg) throws TException {
        return "hello," + msg;
    }

    @Override
    public DemoResult test(DemoParam param) throws TException {
        return new DemoResult("200", param.toString());
    }
}
