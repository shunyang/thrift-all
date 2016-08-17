package com.yangyang.api;

import java.util.concurrent.TimeUnit;

/**
 * 锁接口
 * @author chenshunyang
 *
 */
public interface Lock {
	public void lock(String key);

	public void unlock(String key);
	
	public boolean tryLock(String key, long time, TimeUnit unit);

}
