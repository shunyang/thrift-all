package com.yangyang.thrift.main;


import com.yangyang.thrift.embed.JettyWebServer;
import com.yangyang.thrift.util.FileUtil;
import com.yangyang.thrift.util.JarUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class ThriftServer {

    private static final int PORT = 8080;
    private static final String HOST = "127.0.0.1";
    private static final String CONTEXT_PATH = "/";
    private static final String WEB_DIR = "web";
    private static final String LOG_DIR = "log";
    private static final String TEMP_DIR = "temp";
    private static final Map<String, String> param = new HashMap<>();
    private static Logger logger = LoggerFactory.getLogger(ThriftServer.class);


    public static void main(String... args) throws Exception {

        param.put(WEB_DIR, "web");
        param.put(LOG_DIR, "logs");
        param.put(TEMP_DIR, "temp");


        for (String arg : args) {
            logger.debug(arg);
            if (!StringUtils.isEmpty(arg) && arg.contains("=")) {
                String[] t = arg.trim().split("=");
                param.put(StringUtils.trimLeadingCharacter(t[0].trim(), '-'), t[1]);
            }
        }

        initParam();

        unzipSelf();

        JettyWebServer server = new JettyWebServer(
                PORT,HOST,
                param.get(TEMP_DIR),
                param.get(WEB_DIR),
                param.get(LOG_DIR),
                CONTEXT_PATH);

        server.start();
        server.join();
    }


    private static void initParam() {


        String logDir = FileUtil.currentWorkDir + param.get(LOG_DIR);
        String tempDir = FileUtil.currentWorkDir + param.get(TEMP_DIR);
        String webDir = FileUtil.currentWorkDir + param.get(WEB_DIR);

        logger.debug(logDir);
        logger.debug(tempDir);
        logger.debug(webDir);

        String temp = "x.x";//占位
        FileUtil.createDirs(logDir + "/" + temp);
        FileUtil.createDirs(tempDir + "/" + temp);
        FileUtil.createDirs(webDir + "/" + temp);

        param.put(LOG_DIR, logDir);
        param.put(TEMP_DIR, tempDir);
        param.put(WEB_DIR, webDir);
    }


    private static void unzipSelf() {
        //将jar自身解压

        String selfPath = FileUtil.getJarExecPath(ThriftServer.class);
        if (selfPath.endsWith(".jar")) {
            // 运行环境
            try {
                logger.info("正在将\n" + selfPath + "\n解压至\n" + param.get(WEB_DIR));
                JarUtils.unJar(selfPath, param.get(WEB_DIR));
            } catch (Exception e) {
                logger.error("解压web内容失败!", e);
            }
        } else {
            // IDE环境
            param.put(WEB_DIR, selfPath);
        }
        logger.info(selfPath);
    }
}
