package com.yangyang.thrift.tnonblockingserver;

import com.yangyang.thrift.api.HelloService;
import com.yangyang.thrift.service.HelloServiceImpl;
import org.apache.thrift.TException;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.server.TNonblockingServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;

/**
 * **
 * 注册服务端
 *  使用非阻塞式IO，服务端和客户端需要指定 TFramedTransport 数据传输的方式。 TNonblockingServer
 * Created by chenshunyang on 2016/10/31.
 */
public class HelloTNonblockingServer {
    // 注册端口
    public static final int SERVER_PORT = 8080;

    public static void main(String[] args) throws TException{
        //处理器
        TProcessor processor = new HelloService.Processor<HelloService.Iface>(new HelloServiceImpl());
        // 传输通道 - 非阻塞方式
        TNonblockingServerSocket serverTransport = new TNonblockingServerSocket(SERVER_PORT);
        //异步IO，需要使用TFramedTransport，它将分块缓存读取。
        TNonblockingServer.Args tArgs = new TNonblockingServer.Args(serverTransport);
        tArgs.processor(processor);
        // 使用非阻塞式IO，服务端和客户端需要指定TFramedTransport数据传输的方式
        tArgs.transportFactory(new TFramedTransport.Factory());
        //使用高密度二进制协议
        tArgs.protocolFactory(new TCompactProtocol.Factory());

        TNonblockingServer server = new TNonblockingServer(tArgs);
        System.out.println("HelloTNonblockingServer start....");
        server.serve();


    }
}
