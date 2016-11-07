package com.yangyang.thrift;

import com.yangyang.thrift.api.UserRequest;
import com.yangyang.thrift.api.UserResponse;
import com.yangyang.thrift.api.UserService;
import com.yangyang.thrift.proxy.ThriftClientProxy;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by chenshunyang on 2016/11/1.
 */
public class ClientTest {
    public static void main(String[] args) throws Exception{
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring-applicationContext-client.xml");

        ThriftClientProxy thriftClientProxy = (ThriftClientProxy) context.getBean(ThriftClientProxy.class);
        UserService.Iface client = (UserService.Iface)thriftClientProxy.getClient(UserService.class);

        UserRequest request = new UserRequest();
        request.setId("10000");
        UserResponse urp = client.userInfo(request);
        System.out.println(urp);
    }
}
