package com.yangyang.thrift.connection;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.thrift.protocol.TProtocol;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by chenshunyang on 16/8/15.
 */
public class ProtocolPoolTest {
    public static void main(String[] args) throws Exception{
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxIdle(10);
        config.setMinIdle(1);
        config.setTestOnBorrow(true);

        ObjectPool<TProtocol> pool = new AutoClearGenericObjectPool<TProtocol>(
                new TProtocolFactory("127.0.0.1", 2181, true), config);

        List<TProtocol> list = new ArrayList<TProtocol>();
        for (int i = 1; i <= 10; i++) {
            TProtocol protocol = pool.borrowObject();
            System.out.println(protocol.toString());
            if (i % 2 == 0) {
                //10个连接中，将偶数归还
                pool.returnObject(protocol);
            } else {
                list.add(protocol);
            }
        }

        Random rnd = new Random();
        while (true) {
            System.out.println(String.format("active:%d,idea:%d", pool.getNumActive(), pool.getNumIdle()));
            Thread.sleep(5000);
            //每次还一个
            if (list.size() > 0) {
                int i = rnd.nextInt(list.size());
                pool.returnObject(list.get(i));
                list.remove(i);
            }

            //直到全部还完
            if (pool.getNumActive() <= 0) {
                break;
            }
        }

        System.out.println("clear all------------------------");

        list.clear();
        //连接池为空，测试是否能重新创建新连接
        for (int i = 1; i <= 10; i++) {
            TProtocol protocol = pool.borrowObject();
            System.out.println(protocol.toString());
            if (i % 2 == 0) {
                pool.returnObject(protocol);
            } else {
                list.add(protocol);
            }
        }

        while (true) {
            System.out.println(String.format("active:%d,idea:%d", pool.getNumActive(), pool.getNumIdle()));
            Thread.sleep(5000);
            if (list.size() > 0) {
                int i = rnd.nextInt(list.size());
                pool.returnObject(list.get(i));
                list.remove(i);
            }

            if (pool.getNumActive() <= 0) {
                pool.close();
                break;
            }
        }


    }
}
