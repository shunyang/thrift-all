package com.yangyang.thrift.support.service;

import org.apache.thrift.TProcessor;

/**
 * Created by chenshunyang on 2016/9/23.
 */
public interface ThriftServerService {
    String getName();

    TProcessor getProcessor(ThriftServerService bean);
}
