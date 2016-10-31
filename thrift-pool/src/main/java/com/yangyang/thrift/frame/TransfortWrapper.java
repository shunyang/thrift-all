package com.yangyang.thrift.frame;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

public class TransfortWrapper {
	
	 private TTransport transport;
	 private boolean isBusy = false;//是否正忙
	 private boolean isDead =false;//是否服务挂掉
	 private String host;//服务端主机名或者ip
	 private int port;//服务端端口
	 private Date lastUseTime;//最后使用时间
	 public TransfortWrapper(TTransport transport, String host, int port,boolean isOpen) {
		this.transport = transport;
		this.host = host;
		this.port = port;
		this.lastUseTime =new Date();
		if (isOpen) {
			try {
				transport.open();
			} catch (TTransportException e) {
				//e.printStackTrace();
				System.err.println(host + ":" + port + " " + e.getMessage());
				isDead =true;
			}
		}
	}
	 
	 public TransfortWrapper(TTransport transport, String host, int port) {
		this(transport, host, port, false);
     }
	 /**
	  * 当前transport是否可用
	  * chenshunyang
	  * 2016年8月7日 下午6:06:49
	  */
	 public boolean isAvailable(){
		 return !isBusy && !isDead && transport.isOpen();
	 }
	 
	public TTransport getTransport() {
		return transport;
	}

	public void setTransport(TTransport transport) {
		this.transport = transport;
	}

	public boolean isBusy() {
		return isBusy;
	}

	public void setBusy(boolean isBusy) {
		this.isBusy = isBusy;
	}

	public boolean isDead() {
		return isDead;
	}

	public void setDead(boolean isDead) {
		this.isDead = isDead;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public Date getLastUseTime() {
		return lastUseTime;
	}

	public void setLastUseTime(Date lastUseTime) {
		this.lastUseTime = lastUseTime;
	}

	@Override
	public String toString() {
		 SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		 return "hashCode:" + hashCode() + "," +
		         host + ":" + port + ",isBusy:" + isBusy + ",isDead:" + isDead + ",isOpen:" +
		         transport.isOpen() + ",isAvailable:" + isAvailable() + ",lastUseTime:" + format.format(lastUseTime);
	}
}
