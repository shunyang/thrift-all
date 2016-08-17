package com.yangyang.frame.thrift;

import org.apache.thrift.transport.TSocket;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class PoolTest {
	public static void main(String[] args) throws Exception {
		//初始化一个连接池（poolsize=15,minsize=1,maxIdleSecond=5,checkInvervalSecond=10）
        final ThriftTransportPool pool = new ThriftTransportPool(15, 1, 5, 10, getServers());
        //模拟客户端调用
        createClients(pool);
      //等候清理空闲连接
        Thread.sleep(30000);
        //再模拟一批客户端，验证连接是否会重新增加
        createClients(pool);
        System.out.println("输入任意键退出...");
        System.in.read();
        //销毁连接池
        pool.destory();
	}
	
	private static void createClients(final ThriftTransportPool pool) throws Exception {
		//模拟5个client端
        int clientCount = 5;
        Thread thread[] = new Thread[clientCount];
        FutureTask<String> task[]= new FutureTask[clientCount];
        for (int i = 0; i < task.length; i++) {
			task[i] = new FutureTask<String>(new Callable<String>() {
				public String call() throws Exception {
					TSocket socket = (TSocket) pool.get();//从池中取一个可用连接
					//模拟调用RPC会持续一段时间
                    System.out.println(Thread.currentThread().getName() + " => " + pool.getWrapperInfo(socket));
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    pool.release(socket);//记得每次用完，要将连接释放（恢复可用状态）
					return Thread.currentThread().getName() + " done.";
				}
			});
			thread[i] = new Thread(task[i], "Thread" + i);
		}
        //启用所有client线程
        for (int i = 0; i < clientCount; i++) {
            thread[i].start();
            Thread.sleep(10);
        }
        System.out.println("--------------");

        //等待所有client调用完成
        for (int i = 0; i < clientCount; i++) {
            System.out.println(task[i].get());
            System.out.println(pool);
            System.out.println("--------------");
            thread[i] = null;
        }
	}

	private static List<ServerInfo> getServers() {
        List<ServerInfo> servers = new ArrayList<ServerInfo>();
        servers.add(new ServerInfo("localhost", 2181));
        servers.add(new ServerInfo("localhost", 2182));
        servers.add(new ServerInfo("localhost", 1002));//这一个故意写错的，模拟服务器挂了，连接不上的情景
        return servers;
    }

}
