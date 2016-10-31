package com.yangyang.thrift.tthreadedselectorserver;

import com.yangyang.thrift.api.HelloService;
import com.yangyang.thrift.service.HelloServiceImpl;
import org.apache.thrift.TException;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;

/**
 * 多线程Half-sync/Half-async的服务模型.
 * 需要指定为： TFramedTransport 数据传输的方式
 * Created by chenshunyang on 2016/10/31.
 */
public class HelloTThreadedSelectorServer {
    // 注册端口
    private  final static int SERVER_PORT = 8080;

    public static void main(String[] args) throws TException{
        //处理器
        TProcessor processor = new HelloService.Processor<HelloService.Iface>(new HelloServiceImpl());
        // 传输通道 - 非阻塞方式
        TNonblockingServerSocket serverTransport = new TNonblockingServerSocket(SERVER_PORT);
        // 多线程半同步半异步
        TThreadedSelectorServer.Args tArgs = new TThreadedSelectorServer.Args(serverTransport);
        tArgs.processor(processor);
        // 使用非阻塞式IO，服务端和客户端需要指定TFramedTransport数据传输的方式
        tArgs.transportFactory(new TFramedTransport.Factory());
        //使用高密度二进制协议
        tArgs.protocolFactory(new TCompactProtocol.Factory());
        // 多线程半同步半异步的服务模型
        TThreadedSelectorServer server = new TThreadedSelectorServer(tArgs);
        System.out.println("HelloTThreadedSelectorServer start....");
        server.serve();
    }
}
