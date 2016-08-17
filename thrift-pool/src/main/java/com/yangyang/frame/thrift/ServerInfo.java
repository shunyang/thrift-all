package com.yangyang.frame.thrift;

/**
 * 服务端的基本信息
 * @author chenshunyang
 */
public class ServerInfo {
	
	private String host;
	private int port;
	public ServerInfo(String host, int port) {
		super();
		this.host = host;
		this.port = port;
	}
	
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	@Override
	public String toString() {
		return "ServerInfo [host=" + host + ", port=" + port + "]";
	}

}
