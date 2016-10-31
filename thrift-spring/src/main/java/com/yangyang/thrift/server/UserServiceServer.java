package com.yangyang.thrift.server;

import com.yangyang.thrift.api.UserService;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by chenshunyang on 2016/10/31.
 */
public class UserServiceServer {
    /** 服务的端口 */
    private int servicePort;

    @Autowired
    private UserService.Iface iface;

    public void start() {
        try {
            TServerSocket serverTransport = new TServerSocket(servicePort);
            // 关联处理器与 服务的实现
            TProcessor processor = new UserService.Processor<UserService.Iface>(iface);
            // TBinaryProtocol 二进制编码格式进行数据传输
            // 设置协议工厂为 TBinaryProtocol.Factory
            TBinaryProtocol.Factory proFactory = new TBinaryProtocol.Factory(true, true);
            TThreadPoolServer.Args args = new TThreadPoolServer.Args(serverTransport);
            args.processor(processor);
            args.protocolFactory(proFactory);
            // 多线程服务器端使用标准的阻塞式 I/O
            TServer server = new TThreadPoolServer(args);
            System.out.println("Starting server on port " + servicePort + "......");
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getServicePort() {
        return servicePort;
    }

    public void setServicePort(int servicePort) {
        this.servicePort = servicePort;
    }
}
