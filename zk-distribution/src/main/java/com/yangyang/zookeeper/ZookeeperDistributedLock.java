package com.yangyang.zookeeper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import com.yangyang.api.Lock;
import com.yangyang.exception.LockException;
 
/**
    DistributedLock lock = null;
    try {
        lock = new DistributedLock("127.0.0.1:2182");
        lock.lock("test");
        //do something...
    } catch (Exception e) {
        e.printStackTrace();
    } 
    finally {
        if(lock != null)
            lock.unlock();
    }
 * @author xueliang
 *
 */
public class ZookeeperDistributedLock implements Lock, Watcher{
    private ZooKeeper zk;
    private String root = "/locks";//根
   // private String lockName;//竞争资源的标志
    private String waitNode;//等待前一个锁
    private String myZnode;//当前锁
    private CountDownLatch latch;//计数器
    private int sessionTimeout = 30000;
    private final static String splitStr = "_lock_";
    private List<Exception> exception = new ArrayList<Exception>();
     
    /**
     * 创建分布式锁,使用前请确认config配置的zookeeper服务可用
     * @param config 127.0.0.1:2181
     * @param lockName 竞争资源标志,lockName中不能包含单词lock
     */
    public ZookeeperDistributedLock(String config){
     //   this.lockName = lockName;
        // 创建一个与服务器的连接
    	zk = connect(config);
        createRootNode();
    }
 
    public void lock(String key) {
        if(exception.size() > 0){
            throw new LockException(exception.get(0));
        }
        try {
            if(this.tryLock(key)){
                System.out.println("Thread " + Thread.currentThread().getId() + " " +myZnode + " get lock true");
                return;
            }
            else{
                waitForLock(waitNode, sessionTimeout);//等待锁
            }
        } catch (KeeperException e) {
            throw new LockException(e);
        } catch (InterruptedException e) {
            throw new LockException(e);
        } 
    }
 
    private boolean tryLock(String lockName) {
        try {
            if(lockName.contains(splitStr)){
                throw new LockException("lockName can not contains _lock_");
            }
            //创建临时子节点
            myZnode = zk.create(root + "/" + lockName + splitStr, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);
            System.out.println(myZnode + " is created ");
            //取出所有子节点
            List<String> subNodes = zk.getChildren(root, false);
            //取出所有lockName的锁
            List<String> lockObjNodes = new ArrayList<String>();
            for (String node : subNodes) {
                String _node = node.split(splitStr)[0];
                if(_node.equals(lockName)){
                    lockObjNodes.add(node);
                }
            }
            Collections.sort(lockObjNodes);
//            System.out.println(myZnode + "==" + lockObjNodes.get(0));
            if(myZnode.equals(root+"/"+lockObjNodes.get(0))){
                //如果是最小的节点,则表示取得锁
                return true;
            }
            //如果不是最小的节点，找到比自己小1的节点
            String subMyZnode = myZnode.substring(myZnode.lastIndexOf("/") + 1);
            waitNode = lockObjNodes.get(Collections.binarySearch(lockObjNodes, subMyZnode) - 1);
        } catch (KeeperException e) {
            throw new LockException(e);
        } catch (InterruptedException e) {
            throw new LockException(e);
        }
        return false;
    }
 
 
    private boolean waitForLock(String lower, long waitTime) throws InterruptedException, KeeperException {
        Stat stat = zk.exists(root + "/" + lower,true);
        //判断比自己小一个数的节点是否存在,如果不存在则无需等待锁,同时注册监听
        if(stat != null){
            System.out.println(myZnode + " waiting for " + root + "/" + lower);
            this.latch = new CountDownLatch(1);
            boolean geted=this.latch.await(waitTime, TimeUnit.MILLISECONDS);
            if(geted) {
            	System.out.println(myZnode + " waiting for " + root + "/" + lower+"  success");
            }
            this.latch = null;
        }
        return true;
    }
    
    public boolean tryLock(String key,long time, TimeUnit unit) {
        try {
            if(this.tryLock(key)){
                return true;
            }
            return waitForLock(waitNode,time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
 
    public void unlock(String key) {
        try {
            System.out.println("unlock " + myZnode);
            zk.delete(myZnode,-1);
            myZnode = null;
//            zk.delete(root + "/" + key + splitStr, -1);//根据key来删除，待研究方案
            zk.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }
    
	private ZooKeeper connect(String config){
		try {
			zk = new ZooKeeper(config, sessionTimeout, this);
		} catch (IOException e) {
			exception.add(e);
		}
		return zk;
	}
    
	private void createRootNode(){
		 try {
			Stat stat = zk.exists(root,false);
			if (stat == null) {
				//创建根节点
				zk.create(root, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
				System.out.println("zookeeper root ["+root+" ]node created");
			}
		} catch (KeeperException e) {
			exception.add(e);
			e.printStackTrace();
		} catch (InterruptedException e) {
			exception.add(e);
			e.printStackTrace();
		}
	}
	
	/**
     * zookeeper节点的监视器
     */
    public void process(WatchedEvent event) {
        if(this.latch != null) {  
            this.latch.countDown();  
        }
    }
}