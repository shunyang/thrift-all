package com.yangyang.redis;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yangyang.api.Lock;
import com.yangyang.exception.LockException;

import redis.clients.jedis.Jedis;

public class RedisDistributedLock implements Lock{
	private static Logger logger = LoggerFactory.getLogger(RedisDistributedLock.class);
	private static final int DEFAULT_EXPIRE_TIME = 120;
	
	public RedisDistributedLock(){
		
	}

	public void lock(String key) {
		Jedis jedis = null;
		try {
			jedis = RedisFactory.getJedis();
			while (true) {
				logger.warn("lock key: " + key);
				long lock = jedis.setnx(key, key);
				if (lock == 1) {
					jedis.expire(key, DEFAULT_EXPIRE_TIME);
					logger.warn("set lock success, key: " + key + " on  ThreadId:"+Thread.currentThread().getId());
					break;
				} else {
					logger.warn("set lock fail : " + key + " locked by another business on  ThreadId: "+Thread.currentThread().getId());
					Thread.sleep(100);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new LockException(e);
		} finally {
			RedisFactory.recycleJedis(jedis);
		}
	}

	public void unlock(String key) {
		Jedis jedis = null;
		try {
			jedis = RedisFactory.getJedis();
			jedis.del(key);
			logger.warn("unlock  : " + key + " on  ThreadId: "+Thread.currentThread().getId());
		} catch (Exception e) {
			e.printStackTrace();
			throw new LockException(e);
		} finally {
			RedisFactory.recycleJedis(jedis);
		}
		
	}

	public boolean tryLock(String key,long time, TimeUnit unit) {
		// TODO Auto-generated method stub
		return false;
	}

}

