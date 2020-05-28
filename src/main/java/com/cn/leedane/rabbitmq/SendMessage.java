package com.cn.leedane.rabbitmq;

import com.cn.leedane.rabbitmq.send.ISend;
import com.cn.leedane.utils.SerializeUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;
import org.apache.log4j.Logger;

import java.io.IOException;

public class SendMessage {
	private Logger logger = Logger.getLogger(getClass());
	
	private ISend send;

	public SendMessage(ISend send){
		this.send = send;
	}

	public boolean sendMsg() {
		boolean success = false;
		Channel channel = null;
		try {
			// 创建一个频道
			Connection connection = RabbitConnection.getInstance().getConnection();
			channel = connection.createChannel();
			boolean durable = true; // 队列持久
			/**
			 * 指定一个队列
			 * 第一个参数：队列名字
			 * 第二个参数：队列是否可持久化即重启后该队列是否依然存在
			 * 第三个参数：该队列是否时独占的即连接上来时它占用整个网络连接
			 * 第四个参数：是否自动销毁即当这个队列不再被使用的时候即没有消费者对接上来时自动删除
			 * 第五个参数：其他参数如TTL（队列存活时间）等
			 */
			channel.queueDeclare(send.getQueueName(), durable, false,
					false, null);
			logger.info(getClass().getName() +channel.queueDeclare().getMessageCount());
			// 发送的消息
			//String message = object.getFromUserID() + "@@"+ object.getToUserID() + "@@" +object.getMsg()+"@@"+DateUtil.DateToString(object.getCreateTime());

			//开启消息确认
			channel.confirmSelect();
			//声明式事务
			//channel.txSelect();
			// 往队列中发出一条消息(MessageProperties.PERSISTENT_TEXT_PLAIN指定消息的持久化)
			channel.basicPublish("", send.getQueueName(),
					MessageProperties.PERSISTENT_TEXT_PLAIN, SerializeUtil.serializeObject(send.getQueueObject()));
			//logger.info(object.getFromUserID()+"给"+object.getToUserID()+ "发送的消息:" + message + "'");
			//channel.txCommit();//提交事务

			//异步对发送的消息进行确认
			channel.addConfirmListener(new ConfirmListener() {
				@Override
				public void handleAck(long deliveryTag, boolean multiple) throws IOException {
					logger.info("未确认消息，标识：" + deliveryTag);
				}

				@Override
				public void handleNack(long deliveryTag, boolean multiple) throws IOException {
					logger.info(String.format("已确认消息，标识：%d，多个消息：%b", deliveryTag, multiple));
				}
			});
			//RabbitConnection.getInstance().close();
		} catch (Exception e) {
			/*if(channel != null){
				try {
					channel.txRollback();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}*/
			logger.error("发送消息"+ send.getQueueName() +"队列出现异常", e);
			e.printStackTrace();
		}finally {
			if(channel != null){
				try {
					channel.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return success;
	}
}
