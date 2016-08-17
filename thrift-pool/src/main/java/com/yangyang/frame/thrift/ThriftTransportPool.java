package com.yangyang.frame.thrift;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

public class ThriftTransportPool {
	
	private Semaphore access = null;
	private TransfortWrapper[] pool = null;
	
	int poolSize = 1;//连接池大小
    int minSize = 1;//池中保持激活状态的最少连接个数
    int maxIdleSecond = 300;//最大空闲时间（秒），超过该时间的空闲时间的连接将被关闭
    int checkInvervalSecond = 60;//每隔多少秒，检测一次空闲连接（默认60秒）
    List<ServerInfo> serverInfos;
    boolean allowCheck = true;
    Thread checkThread = null;
	
	/**
     * 连接池构造函数
     *
     * @param poolSize            连接池大小
     * @param minSize             池中保持激活的最少连接数
     * @param maxIdleSecond       单个连接最大空闲时间，超过此值的连接将被断开
     * @param checkInvervalSecond 每隔多少秒检查一次空闲连接
     * @param serverList          服务器列表
     */
    public ThriftTransportPool(int poolSize, int minSize, int maxIdleSecond, int checkInvervalSecond, List<ServerInfo> serverList) {
    	if (poolSize <=0) {
			poolSize = 1;
		}
    	if (minSize > poolSize) {
			minSize = poolSize;
		}
    	if (minSize < 0) {
			minSize = 0;
		}
    	this.poolSize = poolSize;
    	this.minSize = minSize;
    	this.maxIdleSecond =maxIdleSecond;
    	this.checkInvervalSecond = checkInvervalSecond;
    	this.serverInfos = serverList;
    	this.allowCheck = true;
    	init();
    	check();
    }
    
    /**
     * 连接池构造函数（默认最大空闲时间300秒）
     * @param poolSize 连接池大小
     * @param minSize 池中保持激活的最少连接数
     * @param serverList 服务器列表
     */
    public ThriftTransportPool(int poolSize, int minSize, List<ServerInfo> serverList){
    	this(poolSize, minSize, 300, 60, serverList);
    }
    
    public ThriftTransportPool(int poolSize, List<ServerInfo> serverList) {
        this(poolSize, 1, 300, 60, serverList);
    }
    
    public ThriftTransportPool(List<ServerInfo> serverList) {
        this(serverList.size(), 1, 300, 60, serverList);
    }

    /**
     * 检查空闲连接
     * chenshunyang
     * 2016年8月7日 下午5:49:58
     */
	private void check() {
		checkThread = new Thread(new Runnable() {
			public void run() {
				while (allowCheck) {
					 System.out.println("开始检测空闲连接...");
					 for (int i = 0; i < pool.length; i++) {
						if (pool[i].isAvailable() && pool[i].getLastUseTime() != null) {
							long idleTime = new Date().getTime() - pool[i].getLastUseTime().getTime();
							//超过空闲阀值的连接，主动断开，以减少资源消耗
							if (idleTime > maxIdleSecond *1000) {
								if (getActiveCount() > minSize) {
									pool[i].getTransport().close();
									pool[i].setBusy(false);
                                    System.out.println(pool[i].hashCode() + "," + pool[i].getHost() + ":" + pool[i].getPort() + " 超过空闲时间阀值被断开！");
								}
							}
						}
					}
					System.out.println("当前活动连接数：" + getActiveCount());
					try {
						Thread.sleep(checkInvervalSecond *1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		checkThread.start();
	}
	/**
	 * 连接池初始化
	 * chenshunyang
	 * 2016年8月7日 下午5:39:56
	 */
	private void init() {
		access = new Semaphore(poolSize);
		pool = new TransfortWrapper[poolSize];
		for (int i = 0; i < pool.length; i++) {
			int j = i % serverInfos.size();
			TSocket socket = new TSocket(serverInfos.get(j).getHost(), serverInfos.get(j).getPort());
			if (i < minSize) {
				pool[i] = new TransfortWrapper(socket, serverInfos.get(j).getHost(), serverInfos.get(j).getPort(),true);	
			}else{
				pool[i] = new TransfortWrapper(socket, serverInfos.get(j).getHost(), serverInfos.get(j).getPort());
			}
			
		}
		
	}
	/**
	 * 从池中取一个可用连接
	 * chenshunyang
	 * 2016年8月7日 下午6:09:45
	 */
	 public TTransport get(){
		 try {
			if (access.tryAcquire(3, TimeUnit.SECONDS)) {
				synchronized (this) {
					for (int i = 0; i < pool.length; i++) {
						if (pool[i].isAvailable()) {
							pool[i].setBusy(true);
							pool[i].setLastUseTime(new Date());
							return pool[i].getTransport();
						}
					}
					//尝试激活更多连接
					for (int i = 0; i < pool.length; i++) {
						if (!pool[i].isBusy() && !pool[i].isDead() && !pool[i].getTransport().isOpen()) {
							try {
								pool[i].getTransport().open();
								pool[i].setBusy(true);
								pool[i].setLastUseTime(new Date());
								return pool[i].getTransport();
							} catch (TTransportException e) {
								//e.printStackTrace();
								 System.err.println(pool[i].getHost() + ":" + pool[i].getPort() + " " + e.getMessage());
	                             pool[i].setDead(true);
							}
							
						}
					}
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new RuntimeException("can not get available client");
		}
		throw new RuntimeException("all client is too busy");
	 }
	 
	 /**
	  * 客户端调用完成后，必须手动调用此方法，将TTransport恢复为可用状态
	  * chenshunyang
	  * 2016年8月7日 下午6:50:29
	  */
	 public void release(TTransport client){
		 boolean released =false;
		 synchronized (this) {
			 for (int i = 0; i < pool.length; i++) {
				if (pool[i].getTransport() == client && pool[i].isBusy()) {
					pool[i].setBusy(false);
					released = true;
					break;
				}
			}
		 }
		 if (released) {
			access.release();
		}
	 }
	 
	 public void destory(){
		 for (int i = 0; i < pool.length; i++) {
			pool[i].getTransport().close();
		}
		 allowCheck =false;
		 checkThread = null;
		 System.out.print("连接池被销毁！");
	 }
	
	
	/**
	 * 获取当前已经激活的连接数
	 * chenshunyang
	 * 2016年8月7日 下午6:00:10
	 */
	public int getActiveCount() {
		int result =0 ;
		for (int i = 0; i < pool.length; i++) {
			if (!pool[i].isDead() && pool[i].getTransport().isOpen()) {
				result+=1;
			}
		}
		return result;
	}
	/**
	 * 获取当前繁忙状态的连接数
	 * chenshunyang
	 * 2016年8月7日 下午6:03:34
	 */
	public int getBusyCount(){
		int result = 0;
		for (int i = 0; i < pool.length; i++) {
			if (!pool[i].isDead() && pool[i].isBusy()) {
				result++;
			}
		}
		return result;
	}
	
	/**
	 * 获取当前已"挂"掉连接数
	 * chenshunyang
	 * 2016年8月7日 下午6:06:03
	 */
	public int getDeadCount(){
		int result = 0;
		for (int i = 0; i < pool.length; i++) {
			if (pool[i].isDead()) {
				result++;
			}
		}
		return result;
	}
	 public String toString() {
	        return "poolsize:" + pool.length +
	                ",minSize:" + minSize +
	                ",maxIdleSecond:" + maxIdleSecond +
	                ",checkInvervalSecond:" + checkInvervalSecond +
	                ",active:" + getActiveCount() +
	                ",busy:" + getBusyCount() +
	                ",dead:" + getDeadCount();
	    }
	 
	 public String getWrapperInfo(TTransport client){
		 for (int i = 0; i < pool.length; i++) {
			if (pool[i].getTransport() == client) {
				return pool[i].toString();
			}
		}
		 return "";
	 }

}
