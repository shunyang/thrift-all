package com.yangyang.thrift;

import com.yangyang.thrift.server.gen.HelloService;
import com.yangyang.thrift.server.gen.UserService;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;



public class HelloServiceClientTest {

    HelloService.Client client;

    UserService.Client uClient;

    TTransport transport;

    @Before
    public void init() {
        transport = new TSocket("127.0.0.1", 8090);
        try {
            transport.open();
        } catch (TTransportException e) {
            e.printStackTrace();
        }

        TJSONProtocol protocol = new TJSONProtocol(transport);


        TMultiplexedProtocol uProtocol=new TMultiplexedProtocol(protocol,"userServiceProcessor");
        TMultiplexedProtocol hProtocol=new TMultiplexedProtocol(protocol,"helloServiceProcessor");
        client = new HelloService.Client(hProtocol);
        uClient = new UserService.Client(uProtocol);
    }

    @Test
    public void testSayHello() {
        try {
            System.out.println(client.sayHello());

        } catch (TException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFindAll() {
        try {
            System.out.println(uClient.findAll());
        } catch (TException e) {
            e.printStackTrace();
        }
    }

    @After
    public void close() {
        transport.close();
    }

}
