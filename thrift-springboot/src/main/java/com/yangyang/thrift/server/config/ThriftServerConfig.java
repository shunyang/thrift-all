package com.yangyang.thrift.server.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class ThriftServerConfig {

    int port = 8090;

    String ipAddress = "127.0.0.1";

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
