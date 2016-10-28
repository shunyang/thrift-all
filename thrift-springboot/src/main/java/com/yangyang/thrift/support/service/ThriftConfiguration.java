package com.yangyang.thrift.support.service;

import com.yangyang.thrift.support.annotions.EnableThriftServer;
import org.apache.thrift.protocol.TJSONProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.net.InetSocketAddress;


@Configuration
@Conditional(ThriftConfiguration.ThriftServerConfigCondition.class)
public class ThriftConfiguration {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired(required = false)
    TServerTransport tServerTransport;

    @Autowired(required = false)
    TProtocolFactory tProtocolFactory;

    /**
     * 如果没有设置 transport 启用默认配置
     * 
     * @return
     * @throws TTransportException
     */
    @Bean
    @ConditionalOnMissingBean(TServerTransport.class)
    public TServerTransport tServerTransport() throws TTransportException {
        logger.info("init default TServerTransport in port 8090");
        return new TServerSocket(new InetSocketAddress("127.0.0.1", 8090));
    }

    /**
     * 如果没有设置序列化协议,使用JSON协议
     * 
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(TProtocolFactory.class)
    public TProtocolFactory tProtocolFactory() {
        logger.info("init default TProtocol use TJSONProtocol");
        return new TJSONProtocol.Factory();
    }

    protected static class ThriftServerConfigCondition extends SpringBootCondition {

        @Override
        public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
            String[] enablers = context.getBeanFactory().getBeanNamesForAnnotation(EnableThriftServer.class);
            for (String name : enablers) {
                if (context.getBeanFactory().isTypeMatch(name, ThriftServerService.class)) {
                    return ConditionOutcome.match("found @EnableThriftServer on a ThriftServerService");
                }
            }
            return ConditionOutcome.noMatch("found no @EnableThriftServer on a ThriftServerService");
        }
    }
}
