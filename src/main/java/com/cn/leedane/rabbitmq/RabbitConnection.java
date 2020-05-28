package com.cn.leedane.rabbitmq;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * Rabbitmq连接对象
 * @author LeeDane
`* 2016年8月4日 下午6:36:22
 * Version 1.0
 */
public class RabbitConnection {
	private static RabbitConnection rabbitConnection = null;
	private static Connection connection = null;
	private RabbitConnection(){
		//打开连接和创建频道，与发送端一样
		ConnectionFactory factory = new ConnectionFactory();
		// 设置MabbitMQ所在主机ip或者主机名
		factory.setHost("localhost");
		try {
			// 创建一个连接
			connection = factory.newConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}catch (TimeoutException e) {
			e.printStackTrace();
		}
	}
	
	public static synchronized RabbitConnection getInstance(){
		if(rabbitConnection == null){
			synchronized (RabbitConnection.class) {
				if(rabbitConnection == null)
					rabbitConnection = new RabbitConnection();
			}
		}
		
		return rabbitConnection;
	}
	
	/**
	 * 获取连接对象
	 * @return
	 */
	public Connection getConnection(){
		//添加阻塞的监听
		connection.addBlockedListener(new MyBlockedListener());
		//添加挂掉的监听
		connection.addShutdownListener(new MyShutdownListener());
		return connection;
	}
	
	/*public void close(){
		try {
			connection.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/
}
