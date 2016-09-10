package com.yangyang.thrift;

import com.yangyang.thrift.service.DemoEnum;
import com.yangyang.thrift.service.DemoParam;
import com.yangyang.thrift.service.HealthService;
import com.yangyang.thrift.service.HelloService;
import org.apache.http.impl.client.HttpClients;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.*;

/**
 * Created by chenshunyang on 16/9/4.
 */
public class ThriftServiceClientTest {
    static String serviceHost = "127.0.0.1";
    static String baseUrl = "http://" + serviceHost + ":8081/";

    /**
     * @param args
     */
    public static void main(String[] args) {

        testHealthService4Http();

        testHelloService4Http();

        testHealthService4Rpc();

        testHelloService4Rpc();
    }

    /**
     * 测试HelloService(http协议)
     */
    private static void testHelloService4Http() {
        String serviceUrl = baseUrl + "hello.service";
        try {
            System.out.println("serviceUrl:"+serviceUrl);
            HelloService.Client client = new HelloService.Client(getProtocol4Http(serviceUrl));
            System.out.println(client.hello("testHelloService4Http jimmy.yang"));
            client.getInputProtocol().getTransport().close();
            client.getOutputProtocol().getTransport().close();
        } catch (TTransportException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        }
    }


    /**
     * 测试HealthService(http协议)
     */
    private static void testHealthService4Http() {
        String serviceUrl = baseUrl + "health.service";
        try {
            HealthService.Client client = new HealthService.Client(getProtocol4Http(serviceUrl));
            System.out.println(client.ping());
            client.getInputProtocol().getTransport().close();
            client.getOutputProtocol().getTransport().close();
        } catch (TTransportException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        }
    }


    /**
     * 测试HelloService(Rpc方式)
     */
    private static void testHelloService4Rpc() {
        try {
            HelloService.Client client = new HelloService.Client(getProtocol4Rpc(10001));
            System.out.println(client.hello("testHelloService4Rpc jimmy.yang"));
            System.out.println(client.test(getDemoParam()));
            client.getInputProtocol().getTransport().close();
            client.getOutputProtocol().getTransport().close();
        } catch (TTransportException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试HealthService(Rpc方式）
     */
    private static void testHealthService4Rpc() {

        try {
            HealthService.Client client = new HealthService.Client(getProtocol4Rpc(10002));
            System.out.println(client.ping());
            client.getInputProtocol().getTransport().close();
            client.getOutputProtocol().getTransport().close();
        } catch (TTransportException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        }
    }

    private static DemoParam getDemoParam() {
        DemoParam p = new DemoParam();
        p.setDemoEnum(DemoEnum.B);
        p.setId(1);
        p.setName("abcd");
        return p;
    }

    private static TProtocol getProtocol4Http(String url) throws TTransportException {
        return new TCompactProtocol(new THttpClient(url, HttpClients.createMinimal()));
    }

    private static TProtocol getProtocol4Rpc(int port) throws TTransportException {
        TSocket tSocket = new TSocket(serviceHost, port);
        TTransport transport = new TFramedTransport(tSocket);
        transport.open();
        return new TCompactProtocol(transport);
    }
}
