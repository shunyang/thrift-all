package com.yangyang.thrift.tmultiplexedprocessor;

import com.yangyang.thrift.api.HelloService;
import com.yangyang.thrift.api.User;
import com.yangyang.thrift.api.UserService;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

/**
 * 客户端调用多个service一起
 * 非阻塞
 * Created by chenshunyang on 2016/10/31.
 */
public class HelloTThreadedSelectorClient {
    public static final String SERVER_IP = "127.0.0.1";
    public static final int SERVER_PORT = 8080;
    public static final int TIMEOUT = 30000;

    public static void main(String[] args) throws TException {
        //设置传输通道，对于非阻塞服务，需要使用TFramedTransport，它将数据分块发送
        TTransport transport = new TFramedTransport(new TSocket(SERVER_IP,SERVER_PORT,TIMEOUT));

        TProtocol protocol = new TCompactProtocol(transport);
        transport.open();

        // 0.9.1版本以上支持多服务接口共用实现,这里也必须保持和服务端一致
        TMultiplexedProtocol tpHelloService = new TMultiplexedProtocol(protocol,"HelloService");
        HelloService.Client client = new HelloService.Client(tpHelloService);
        String result = client.hello("jack");
        System.out.println("result : " + result);

        TMultiplexedProtocol tpUserService = new TMultiplexedProtocol(protocol,"UserService");
        UserService.Client userService = new UserService.Client(tpUserService);
        User user = userService.findUser();
        System.out.println(user);

        //关闭资源
        transport.close();
    }
}
