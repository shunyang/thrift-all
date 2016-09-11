package com.yangyang.thrift.spring.client;

import com.alibaba.fastjson.JSONObject;
import com.yangyang.thrift.spring.common.ThriftSpringConst;
import org.apache.thrift.protocol.*;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * thrift client proxy
 *
 */
public class ThriftClientProxy implements InvocationHandler{
	
	private Logger thriftClientLogger = LoggerFactory.getLogger(ThriftSpringConst.thriftLog) ;
	
	private String serverName ;
	
	private Class<?> thriftImpl ;
	
	private String serverIp ;
	private int serverPort ;
	private int timeOut ;
	
	private String protocolType ;
	
	public ThriftClientProxy(String serverName, Class<?> thriftImpl, String serverIp, int serverPort, int timeOut, String protocolType) {
		this.serverName = serverName ;
		this.thriftImpl = thriftImpl ;
		this.serverIp = serverIp ;
		this.serverPort = serverPort ;
		this.timeOut = timeOut ;
		this.protocolType = protocolType ;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		
		StringBuffer logMsg = new StringBuffer("ThriftClientProxy.invoke ") ;
		long startTime = System.currentTimeMillis() ;
		
		TTransport transport = null ;
		Object rt = null ;
		try {
			logMsg.append(serverName).append(".").append(method.getName()).append(getArgsStr(args)) ;

			transport = new TFramedTransport(new TSocket(serverIp, serverPort, timeOut));
			// 必须open才可用
			transport.open(); 
			// 协议要和服务端一致
			TProtocol protocol = packageProtocol(protocolType, transport) ;
			// 0.9.1版本以上支持多服务接口共用实现,这里也必须保持和服务端一致
			TMultiplexedProtocol tp = new TMultiplexedProtocol(protocol, serverName);

			Object target = thriftImpl.getConstructor(org.apache.thrift.protocol.TProtocol.class).newInstance(tp) ;
			rt = method.invoke(target, args);
			
			logMsg.append(" result:").append(JSONObject.toJSON(rt)) ;
			thriftClientLogger.info(logMsg.toString());
			thriftClientLogger.info("ThriftClientProxy.invoke " + serverName + "." + method.getName() + " spendTime:"
                    + (System.currentTimeMillis() - startTime) + " ms.");

		} catch (Exception e) {
			thriftClientLogger.error(logMsg + " error.", e);
		} finally {
			if(transport != null)
				transport.close(); 
		}
		return rt ;
	}
	
	private TProtocol packageProtocol(String protocoltype, TTransport transport) {
		
		if(protocoltype.equals("binary")) {
			return new TBinaryProtocol(transport);
		} else if(protocoltype.equals("json")) {
			return new TJSONProtocol(transport);
		} else if(protocoltype.equals("compact")) {
			return new TCompactProtocol(transport);
		}
		return new TCompactProtocol(transport);
	}

    // 获得参数值
    private String getArgsStr(Object args[]) {

        StringBuffer stringBuffer = new StringBuffer() ;
        stringBuffer.append("(") ;

        if(args != null) {
            for(int i=0; i<args.length; i++) {
                Object object = args[i] ;
                if(null == object){
                    object = "null";
                }
                stringBuffer.append(JSONObject.toJSON(object)) ;
                if(i < args.length - 1) {
                    stringBuffer.append(",") ;
                }
            }
        }

        stringBuffer.append(")") ;

        return stringBuffer.toString() ;
    }
}
