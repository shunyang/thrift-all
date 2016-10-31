package com.yangyang.thrift.thshaserver;

import com.yangyang.thrift.api.HelloService;
import com.yangyang.thrift.service.HelloServiceImpl;
import org.apache.thrift.TException;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.THsHaServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;

/**
 * /**
 * 注册服务端 半同步半异步的服务端模型，需要指定为： TFramedTransport 数据传输的方式。 THsHaServer
 * 非阻塞
 * Created by chenshunyang on 2016/10/31.
 */
public class HelloTHsHaServer {

    // 注册端口
    public static final int SERVER_PORT = 8080;

    public static void main(String[] args) throws TException{
        TProcessor processor = new HelloService.Processor<HelloService.Iface>(new HelloServiceImpl());
        // 传输通道 - 非阻塞方式
        TNonblockingServerSocket serverTransport = new TNonblockingServerSocket(SERVER_PORT);
        //半同步半异步
        THsHaServer.Args tArgs = new THsHaServer.Args(serverTransport);
        tArgs.processor(processor);
        tArgs.transportFactory(new TFramedTransport.Factory());
        //二进制协议
        tArgs.protocolFactory(new TBinaryProtocol.Factory());
        THsHaServer server = new THsHaServer(tArgs);
        System.out.println("HelloTHsHaServer start....");
        server.serve();
    }
}
