package com.yangyang.thrift.support.service;

import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.lang.instrument.IllegalClassFormatException;
import java.util.Map;

/**
 * thrift 接口代理类,组装server实现
 *
 **/
public class ThriftServiceServerProxyBean implements InitializingBean, DisposableBean {

    Logger logger = LoggerFactory.getLogger(getClass());

    private TServerTransport tServerTransport;

    private TProtocolFactory tProtocolFactory;

    private TMultiplexedProcessor processor = new TMultiplexedProcessor();

    private Map<String, TProcessor> processorMap;

    private TThreadPoolServer server;

    public ThriftServiceServerProxyBean(TServerTransport tServerTransport, TProtocolFactory tProtocolFactory,
            Map<String, TProcessor> processorMap) {
        this.tServerTransport = tServerTransport;
        this.tProtocolFactory = tProtocolFactory;
        this.processorMap = processorMap;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (null == tServerTransport) {
            throw new IllegalClassFormatException("tServerTransport is null");
        }
        if (null == tProtocolFactory) {
            throw new IllegalClassFormatException("tProtocolFactory is null");
        }
        if (null == processorMap || processorMap.isEmpty()) {
            throw new IllegalClassFormatException("processorMap is null");
        }

        for (String processorName : processorMap.keySet()) {
            processor.registerProcessor(processorName, processorMap.get(processorName));
            logger.debug("Register a Processor {}", processorName);
        }

        TThreadPoolServer.Args args = new TThreadPoolServer.Args(tServerTransport);
        args.processor(processor);
        args.protocolFactory(tProtocolFactory);
        server = new TThreadPoolServer(args);
        logger.debug("Thrift Server 正在启动............");
        server.serve();
        logger.error("Thrift Server 停止............");
    }

    @Override
    public void destroy() throws Exception {
        server.stop();
        server = null;

        logger.warn("Thrift Server  stop");
    }
}
