package com.yangyang.thrift.proxy;

import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.server.TNonblockingServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.lang.reflect.Constructor;

public class ThriftServerProxy {


    private static Logger logger = LoggerFactory.getLogger(ThriftServerProxy.class);

    private int port;// 端口

    private String serviceInterface;// 实现类接口

    private Object serviceImplObject;//实现类

    private String serviceIface;//接口

    public Object getServiceImplObject() {
        return serviceImplObject;
    }

    public void setServiceImplObject(Object serviceImplObject) {
        this.serviceImplObject = serviceImplObject;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }


    public String getServiceInterface() {
        return serviceInterface;
    }

    public void setServiceInterface(String serviceInterface) {
        this.serviceInterface = serviceInterface;
    }


    public String getServiceIface() {
        return serviceIface;
    }

    public void setServiceIface(String serviceIface) {
        this.serviceIface = serviceIface;
    }


    public void start() {
        new Thread() {
            public void run() {

                try {
                    Class Processor = Class.forName(getServiceInterface() + "$Processor");
                    Class Iface = Class.forName(StringUtils.hasText(getServiceIface()) ?
                            getServiceIface() : getServiceInterface() + "$Iface");
                    Constructor constructor = Processor.getConstructor(Iface);
                    TProcessor processor = (TProcessor) constructor.newInstance(serviceImplObject);

                    TNonblockingServerSocket transport = new TNonblockingServerSocket(getPort());
                    TNonblockingServer.Args tArgs = new TNonblockingServer.Args(transport);
                    tArgs.processor(processor);
                    tArgs.protocolFactory(new TCompactProtocol.Factory());
                    tArgs.transportFactory(new TFramedTransport.Factory());
                    TServer server = new TNonblockingServer(tArgs);

                    logger.info(serviceInterface + "服务启动成功,端口:" + getPort());
                    server.serve();

                } catch (TTransportException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }


}
