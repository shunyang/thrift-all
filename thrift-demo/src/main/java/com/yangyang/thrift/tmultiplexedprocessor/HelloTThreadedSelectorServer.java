package com.yangyang.thrift.tmultiplexedprocessor;

import com.yangyang.thrift.api.HelloService;
import com.yangyang.thrift.api.UserService;
import com.yangyang.thrift.service.HelloServiceImpl;
import com.yangyang.thrift.service.UserServiceImpl;
import org.apache.thrift.TException;
import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;

/**
 * 多线程Half-sync/Half-async的服务模型，需要指定为： TFramedTransport 数据传输的方式
 * 服务端提供对多接口服务支持
 * Created by chenshunyang on 2016/10/31.
 */
public class HelloTThreadedSelectorServer {
    // 注册端口
    private  final static int SERVER_PORT = 8080;
    /**
     * 处理请求线程数量
     */
    private static int selectorThreadNum = 5;

    /**
     * 工作线程数量
     */
    private static int workerThreadNum = 64;

    public static void main(String[] args) throws TException{
        // 传输通道 - 非阻塞方式
        TNonblockingServerSocket serverTransport = new TNonblockingServerSocket(SERVER_PORT);

        // 多线程半同步半异步
        TThreadedSelectorServer.Args tArgs = new TThreadedSelectorServer.Args(serverTransport);
        // 使用非阻塞式IO，服务端和客户端需要指定TFramedTransport数据传输的方式
        tArgs.transportFactory(new TFramedTransport.Factory());
        //使用高密度二进制协议
        tArgs.protocolFactory(new TCompactProtocol.Factory());
        //设置processor工厂
        TMultiplexedProcessor tprocessor = new TMultiplexedProcessor();
        registerServices(tprocessor);
        tArgs.processorFactory(new TProcessorFactory(tprocessor));

        //线程关键参数设置：最好是从配置文件中读取,方便线上修改控制
        //参数默认值:selectorThreads = 2;workerThreads = 5;stopTimeoutVal = 60;
        tArgs.selectorThreads(selectorThreadNum);//处理请求selector线程数
        tArgs.workerThreads(workerThreadNum);//工作线程数


        //采用:TThreadedSelectorServer,高并发，多线程，非阻塞，必须配合TFramedTransport，适用于长连接和短链接场景，推荐总是使用
        TServer server = new TThreadedSelectorServer(tArgs);

        System.out.println("HelloTThreadedSelectorServer start....");
        server.serve();
    }

    /**
     * 服务注册统一入口
     * @param processor
     */
    private static  void registerServices(TMultiplexedProcessor processor) {
        //  这里可以注册多个service服务入口
        processor.registerProcessor("HelloService", new HelloService.Processor<HelloService.Iface>(new HelloServiceImpl()));
        processor.registerProcessor("UserService", new UserService.Processor<UserService.Iface>(new UserServiceImpl()));
    }
}
