package com.yangyang.thrift.server.thrift;

import com.yangyang.thrift.server.gen.HelloService;
import com.yangyang.thrift.support.annotions.EnableThriftServer;
import com.yangyang.thrift.support.service.ThriftServerService;
import org.apache.thrift.TException;
import org.apache.thrift.TProcessor;
import org.springframework.stereotype.Service;

@Service
@EnableThriftServer(genClass = HelloService.class)
public class HelloServiceImpl implements HelloService.Iface, ThriftServerService {

    @Override
    public String getName() {
        return "helloService";
    }

    @Override
    public TProcessor getProcessor(ThriftServerService bean) {
        HelloService.Iface impl = (HelloService.Iface) bean;
        return new HelloService.Processor<HelloService.Iface>(impl);
    }

    @Override
    public String sayHello() throws TException {
        return "Hello,World";
    }

    @Override
    public String sayName(String name) throws TException {
        return "hello," + name;
    }
}
