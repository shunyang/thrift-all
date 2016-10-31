package com.yangyang.thrift;

import com.yangyang.thrift.server.UserServiceServer;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by chenshunyang on 2016/10/31.
 */
public class ServerTest {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring-applicationContext-server.xml");

        UserServiceServer userServiceServer = context.getBean(UserServiceServer.class);
        userServiceServer.start();

    }
}
