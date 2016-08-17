package com.yangyang.zookeeper;

public class ZookeeperDistributedLockTest {
	public static void main(String[] args) {
		Runnable[] tasks = new Runnable[3];
        for(int i=0;i<tasks.length;i++){
        	Runnable task = new Runnable(){
                public void run() {
                    ZookeeperDistributedLock lock = null;
                    String lockName = "test5";//传入锁的名称
                    try {
                        lock = new ZookeeperDistributedLock("127.0.0.1:2181");
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
