package com.yangyang.thrift.listener;

import com.yangyang.thrift.proxy.ThriftServerProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.List;

public class ThriftServerStartListener implements ServletContextListener {
    private static Logger logger = LoggerFactory.getLogger(ThriftServerStartListener.class);


    @Override
    public void contextDestroyed(ServletContextEvent event) {

    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        try {
            ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(event.getServletContext());
            List<ThriftServerProxy> list = ((List<ThriftServerProxy>) context.getBean("thriftServerList"));
            if (list != null && list.size() > 0) {
                list.forEach(ThriftServerProxy::start);
            }
            logger.info("Thrift Server监听器启动成功!");
        } catch (Exception e) {
            logger.error("Thrift Server监听器启动错误", e);
            e.printStackTrace();
        }
    }

}
