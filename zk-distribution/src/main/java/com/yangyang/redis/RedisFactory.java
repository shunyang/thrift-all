package com.yangyang.redis;

import com.yangyang.exception.LockException;
import com.yangyang.util.PropertiesUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisFactory {
	
	private static JedisPool pool;

	// 静态代码初始化池配置
	static {
		try {
			// 创建jedis池配置实例
			JedisPoolConfig config = new JedisPoolConfig();
			// 设置池配置项值
			config.setMaxIdle(Integer.valueOf(PropertiesUtil.get("jedis.pool.maxIdle")));
			config.setMaxWaitMillis(Long.valueOf(PropertiesUtil.get("jedis.pool.maxWait")));
			config.setTestOnBorrow(Boolean.valueOf(PropertiesUtil.get("jedis.pool.testOnBorrow")));
			config.setTestOnReturn(Boolean.valueOf(PropertiesUtil.get("jedis.pool.testOnReturn")));
			// 根据配置实例化jedis池
			pool = new JedisPool(config, PropertiesUtil.get("redis.ip"), Integer.valueOf(PropertiesUtil.get("redis.port")), 2000, null,Integer.valueOf(PropertiesUtil.get("redis.database")));
		} catch (Exception e) {
			e.printStackTrace();
			throw new LockException(e);
		}
	}

	/**
	 * 得到jedis
	 * 
	 * @return
	 */
	public static Jedis getJedis() {
		return pool.getResource();
	}

	/**
	 * 释放jedis
	 * 
	 * @param jedis
	 */
	public static void recycleJedis(Jedis jedis) {
		pool.returnResource(jedis);
	}

}
