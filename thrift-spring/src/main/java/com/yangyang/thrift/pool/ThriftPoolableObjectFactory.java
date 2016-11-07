package com.yangyang.thrift.pool;

import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  连接池工厂
 * Created by chenshunyang on 2016/11/1.
 */
public class ThriftPoolableObjectFactory implements PoolableObjectFactory<TTransport> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /** 服务的IP */
    private String serviceIP;
    /** 服务的端口 */
    private int servicePort;
    /** 超时设置 */
    private int timeOut;

    public ThriftPoolableObjectFactory(String serviceIP, int servicePort, int timeOut) {
        super();
        this.serviceIP = serviceIP;
        this.servicePort = servicePort;
        this.timeOut = timeOut;
    }

    /**
     * 创建对象
     * @return
     * @throws Exception
     */
    @Override
    public TTransport makeObject() throws Exception {
        try {
            TTransport transport = new TSocket(this.serviceIP, this.servicePort, this.timeOut);
            transport.open();
            return transport;
        } catch (Exception e) {
            logger.error("error ThriftPoolableObjectFactory()", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 销毁对象
     * @param tTransport
     * @throws Exception
     */
    @Override
    public void destroyObject(TTransport tTransport) throws Exception {
        if (tTransport.isOpen()) {
            tTransport.close();
        }
    }

    /**
     * 检验对象是否可以由pool安全返回
     * @param tTransport
     * @return
     */
    @Override
    public boolean validateObject(TTransport tTransport) {
        try {
            if (tTransport instanceof TSocket) {
                TSocket thriftSocket = (TSocket) tTransport;
                if (thriftSocket.isOpen()) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 激活对象
     * @param tTransport
     * @throws Exception
     */
    @Override
    public void activateObject(TTransport tTransport) throws Exception {

    }

    /**
     * 使无效 以备后用
     * @param tTransport
     * @throws Exception
     */
    @Override
    public void passivateObject(TTransport tTransport) throws Exception {

    }
}
