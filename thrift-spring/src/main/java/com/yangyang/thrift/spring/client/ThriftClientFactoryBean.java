package com.yangyang.thrift.spring.client;

import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;

/**
 * thrift client factory bean
 */
public class ThriftClientFactoryBean<T> implements FactoryBean<T> {

	private Class<T> thriftInterface;
	private String serverName;
	private Class<?> thriftImpl;
	
	private String serverIp ;
	private int serverPort ;
	private int timeOut ;
	private String protocolType ;
	
	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}
	
	public void setProtocolType(String protocolType) {
		this.protocolType = protocolType;
	}

	public void setThriftInterface(Class<T> thriftInterface) {
		this.thriftInterface = thriftInterface;
	}

	public void setThriftImpl(Class<?> thriftImpl) {
		this.thriftImpl = thriftImpl;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	@SuppressWarnings("unchecked")
	public T getObject() throws Exception {
		
		ThriftClientProxy proxy = new ThriftClientProxy(serverName, thriftImpl, serverIp, serverPort, timeOut, protocolType);
		return (T) Proxy
				.newProxyInstance(thriftInterface.getClassLoader(), new Class[] { thriftInterface }, proxy);
	}

	public Class<T> getObjectType() {
		return this.thriftInterface;
	}

	public boolean isSingleton() {
		return true;
	}
}