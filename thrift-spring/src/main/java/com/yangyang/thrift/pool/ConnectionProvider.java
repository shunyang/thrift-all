package com.yangyang.thrift.pool;

import org.apache.thrift.transport.TSocket;

/**
 * Created by chenshunyang on 2016/11/1.
 */
public interface ConnectionProvider {
    /**
     * 取链接池中的一个链接
     * @return TSocket
     */
    TSocket getConnection();

    /**
     * 返回链接
     * @param socket
     */
    void returnCon(TSocket socket);
}
