package com.yangyang.thrift.ttrhreadpoolserver;

import com.yangyang.thrift.api.HelloService;
import com.yangyang.thrift.service.HelloServiceImpl;
import org.apache.thrift.TException;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;

/**
 * Created by chenshunyang on 2016/10/31.
 */
public class HelloTThreadPoolServer {
    // 注册端口
    private  final static int SERVER_PORT = 8080;

    public static void main(String[] args) throws TException{
        TProcessor processor = new HelloService.Processor<HelloService.Iface>(new HelloServiceImpl());
        // 阻塞IO
        TServerSocket serverTransport = new TServerSocket(SERVER_PORT);
        //多线程服务模型
        TThreadPoolServer.Args tArgs = new TThreadPoolServer.Args(serverTransport);
        tArgs.processor(processor);
        //客户端协议要一致
        tArgs.protocolFactory(new TBinaryProtocol.Factory());

        // 线程池服务模型，使用标准的阻塞式IO，预先创建一组线程处理请求。
        TServer server = new TThreadPoolServer(tArgs);
        System.out.println("HelloTThreadPoolServer start....");
        // 启动服务
        server.serve();
    }
}
