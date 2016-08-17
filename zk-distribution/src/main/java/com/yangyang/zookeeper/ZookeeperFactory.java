package com.yangyang.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ZookeeperFactory{
	private static CountDownLatch latch = new CountDownLatch(1);
	
	private static int sessionTimeout = 30000;
	
	public static ZooKeeper connect(String config){
		ZooKeeper zk = null;
		try {
			zk = new ZooKeeper(config, sessionTimeout, new Watcher(){
				public void process(WatchedEvent event) {
					if (event.getState() == KeeperState.SyncConnected) {
						latch.countDown();
					}
				}
				
			});
			latch.await();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return zk;
	}
	
	public static void createRootNode(ZooKeeper zk,String root){
		 try {
			Stat stat = zk.exists(root,false);
			if (stat == null) {
				//创建根节点
				zk.create(root, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
				System.out.println("zookeeper root ["+root+" ]node created");
			}
		} catch (KeeperException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
