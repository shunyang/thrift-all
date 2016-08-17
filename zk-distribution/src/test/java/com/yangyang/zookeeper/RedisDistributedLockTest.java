package com.yangyang.zookeeper;

import com.yangyang.redis.RedisDistributedLock;

public class RedisDistributedLockTest {
	public static void main(String[] args) {
		Runnable[] tasks = new Runnable[3];
        for(int i=0;i<tasks.length;i++){
        	Runnable task = new Runnable(){
                public void run() {
                    RedisDistributedLock lock = null;
                    String lockName = "redistest";//传入锁的名称
                    try {
                        lock = new RedisDistributedLock();
                        lock.lock(lockName);
                        System.out.println("Thread " + Thread.currentThread().getId() + " running");
                    } catch (Exception e) {
                        e.printStackTrace();
                    } 
                    finally {
                        lock.unlock(lockName);
                    }
                     
                }
            };
            tasks[i] = task;
        }
        new ConcurrentTest(tasks);
	}

}
