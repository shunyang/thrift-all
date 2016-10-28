package com.yangyang.thrift.main;

import com.yangyang.thrift.support.listener.ThriftApplicationListener;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Created by chenshunyang on 2016/9/21.
 */
@EnableAutoConfiguration
@Configuration
@ComponentScan(basePackages = "com.yangyang.thrift")
@EnableAspectJAutoProxy
public class Main {
    public static void main(String[] args) {
        new SpringApplicationBuilder().listeners(new ThriftApplicationListener()).sources(Main.class).run(args);
    }
}
