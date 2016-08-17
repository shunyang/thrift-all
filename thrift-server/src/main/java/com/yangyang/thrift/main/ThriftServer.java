package com.yangyang.thrift.main;

import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.eclipse.jetty.servlets.GzipFilter;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.DispatcherType;
import java.io.IOException;
import java.util.EnumSet;

/**
 * Created by chenshunyang on 16/8/16.
 */
public class ThriftServer {

    private static final Logger Log = LoggerFactory.getLogger(ThriftServer.class);

    public static final String CONTEXT_PATH = "/thrift-server";
    private static final String DEFAULT_HOST = "127.0.0.1";
    private static final int DEFAULT_PORT = 9090;

    public static void main(String[] args) {
        try {
            new ThriftServer().startJetty(args);
        } catch (Exception e) {
            Log.error("start jetty server fail", e);
        }
    }

    private void startJetty(String[] args) throws Exception {
        Server server = new Server(createThreadPool());
        server.setConnectors(createConnector(server));
        server.setHandler(createHandlers());
        server.start();
        Log.info("start ThriftServer success, content:{}, host:{},port:{}", CONTEXT_PATH,DEFAULT_HOST, DEFAULT_PORT);
        server.join();
    }

    private QueuedThreadPool createThreadPool() {
        QueuedThreadPool threadPool = new QueuedThreadPool();
        threadPool.setMinThreads(3);
        threadPool.setMaxThreads(200);
        return threadPool;
    }

    private Connector[] createConnector(Server server) {
        HttpConfiguration http_config = createHttpConfiguration();
        ServerConnector connector = new ServerConnector(server, new HttpConnectionFactory(http_config));
        connector.setPort(DEFAULT_PORT);
        connector.setHost(DEFAULT_HOST);
        connector.setIdleTimeout(30000);
        return new Connector[]{connector};
    }

    private HttpConfiguration createHttpConfiguration() {
        HttpConfiguration http_config = new HttpConfiguration();
        http_config.setSecureScheme("https");
        http_config.setSecurePort(8443);
        http_config.setOutputBufferSize(32768);
        http_config.setRequestHeaderSize(8192);
        http_config.setResponseHeaderSize(8192);
        return http_config;
    }

    private static ServletContextHandler createHandlers() throws IOException {
        ServletContextHandler contextHandler = new ServletContextHandler();
        contextHandler.setErrorHandler(null);
        contextHandler.setContextPath(CONTEXT_PATH);

        // 配置支持跨域
        FilterHolder crossOriginFilterHolder = new FilterHolder(new CrossOriginFilter());
        // TODO 生产环境应限制跨域访问
        crossOriginFilterHolder.setInitParameter("allowedOrigins", "*");
        crossOriginFilterHolder.setInitParameter("allowedMethods", "GET,POST,PUT,HEAD");
        crossOriginFilterHolder.setInitParameter("allowedHeaders", "Content-Type,Accept,Origin,Authorization,X-Requested-With");
        contextHandler.addFilter(crossOriginFilterHolder, "/*", EnumSet.of(DispatcherType.REQUEST));


//        ResourceHandler handler = new ResourceHandler();  //静态资源处理的handler
//        handler.setDirectoriesListed(true);  //会显示一个列表
//        handler.setWelcomeFiles(new String[]{"index1.html"});
//        handler.setResourceBase("WebContent");
//        server.setHandler(handler);

        ServletHolder dispatcher = new ServletHolder(new DispatcherServlet());
        dispatcher.setInitParameter("contextConfigLocation", "classpath:spring-application.xml");
        dispatcher.setAsyncSupported(true);
        dispatcher.setInitOrder(1);
        contextHandler.addServlet(dispatcher, "/");

        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");
        characterEncodingFilter.setForceEncoding(true);
        contextHandler.addFilter(new FilterHolder(characterEncodingFilter), "/*", EnumSet.of(DispatcherType.REQUEST));

        GzipFilter gzipFilter = new GzipFilter();
        FilterHolder gizpHolder = new FilterHolder(gzipFilter);
        gizpHolder.setInitParameter("mimeTypes", "application/json;charset=UTF-8");
        contextHandler.addFilter(gizpHolder, "/*", EnumSet.of(DispatcherType.REQUEST));
        //配置支持对token的权限判断
        return contextHandler;
    }
}


