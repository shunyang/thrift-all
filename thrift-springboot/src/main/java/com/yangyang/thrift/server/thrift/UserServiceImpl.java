package com.yangyang.thrift.server.thrift;

import com.yangyang.thrift.server.gen.UserService;
import com.yangyang.thrift.support.annotions.EnableThriftServer;
import com.yangyang.thrift.support.service.ThriftServerService;
import org.apache.thrift.TException;
import org.apache.thrift.TProcessor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;


@Service
@EnableThriftServer(genClass = UserService.class)
public class UserServiceImpl implements UserService.Iface, ThriftServerService {

    @Override
    public String getName() {
        return "userService";
    }

    @Override
    public TProcessor getProcessor(ThriftServerService bean) {
        UserService.Iface impl = (UserService.Iface) bean;
        return new UserService.Processor<UserService.Iface>(impl);
    }

    @Override
    public List<String> findAll() throws TException {
        return Arrays.asList(new String[] { "a", "b" });
    }
}
