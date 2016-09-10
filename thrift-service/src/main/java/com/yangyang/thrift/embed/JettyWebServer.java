package com.yangyang.thrift.embed;

import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.servlet.ErrorPageErrorHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.LoggerFactory;

import java.io.File;

public class JettyWebServer {

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(JettyWebServer.class);

    private Server server;
    private int port;
    private String host;
    private String tempDir;
    private String logDir;
    private String webDir;
    private String contextPath;


    public JettyWebServer(int port, String host, String tempDir, String webDir, String logDir, String contextPath) {

        logger.info("port:{},host:{},tempDir:{},webDir:{},logDir:{},contextPath:{}", port, host, tempDir, webDir, logDir, contextPath);

        this.port = port;
        this.host = host;
        this.tempDir = tempDir;
        this.webDir = webDir;
        this.contextPath = contextPath;
        this.logDir = logDir;
    }

    public void start() throws Exception {
        server = new Server(createThreadPool());
        server.addConnector(createConnector());
        server.setHandler(createHandlers());
        server.setStopAtShutdown(true);
        server.start();
    }

    public void join() throws InterruptedException {
        server.join();
    }


    private ThreadPool createThreadPool() {
        QueuedThreadPool threadPool = new QueuedThreadPool();
        threadPool.setMinThreads(10);
        threadPool.setMaxThreads(100);
        return threadPool;
    }


    private NetworkConnector createConnector() {
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(port);
        connector.setHost(host);
        return connector;
    }

    private HandlerCollection createHandlers() {
        WebAppContext context = new WebAppContext();
        context.setContextPath(contextPath);
        context.setWar(webDir);
        context.setTempDirectory(new File(tempDir));

        ErrorPageErrorHandler errorPage = new ErrorPageErrorHandler();
        errorPage.addErrorPage(404,"/404");
        context.setErrorHandler(errorPage);

        RequestLogHandler logHandler = new RequestLogHandler();
        logHandler.setRequestLog(createRequestLog());

        HandlerCollection handlerCollection = new HandlerCollection();
        handlerCollection.setHandlers(new Handler[]{context, logHandler});
        return handlerCollection;
    }

    private RequestLog createRequestLog() {
        //记录访问日志的处理
        NCSARequestLog requestLog = new NCSARequestLog();
        requestLog.setFilename(logDir + "/thrift-service-access_yyyy_mm_dd.log");
        requestLog.setRetainDays(90);
        requestLog.setExtended(false);
        requestLog.setAppend(true);
        //requestLog.setLogTimeZone("GMT");
        requestLog.setLogTimeZone("Asia/Shanghai");
        requestLog.setLogDateFormat("yyyy-MM-dd HH:mm:ss SSS");
        requestLog.setLogLatency(true);
        return requestLog;
    }

}
