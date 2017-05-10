package com.cn.leedane.rabbitmq;

import org.apache.log4j.Logger;

import com.cn.leedane.rabbitmq.recieve.IRecieve;
import com.cn.leedane.utils.SerializeUtil;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;

public class RecieveMessage {
	private Logger logger = Logger.getLogger(getClass());
	
	private IRecieve recieve;
	public RecieveMessage(IRecieve recieve) {
		this.recieve = recieve;
	}
	
	/**
	 * 获取消息
	 * @param queueName 队列的名称
	 * @return
	 * @throws Exception
	 */
	public void getMsg() throws Exception{
		//打开连接和创建频道，与发送端一样
		Channel channel = RabbitConnection.getInstance().getConnection().createChannel();
		//声明队列，主要为了防止消息接收者先运行此程序，队列还不存在时创建队列。
		//channel.queueDeclare(QUEUE_NAME, false, false, false, null);
		logger.info("queueName:"+recieve.getQueueName()+"正在等待消息");
		//创建队列消费者
		QueueingConsumer consumer = new QueueingConsumer(channel);
		//指定消费队列
		
		/**
		 * 解决阻塞的办法，可以在subscribe消息队列是设置autoAck=true，这样会避免消息队列中消息阻塞，
		 * 这种情况是worker接到消息后，就会把消息从消息队列删除，不管消息是否被正确处理，另一种是设置autoAck=false，
		 * 这样worker在接受消息后，必须给予服务端一个ack响应，该消息才会从消息队列中删除，这样会防止消息的意外丢失，
		 * 但要注意的是，消息队列如果没有接收到ack响应，该消息对了的消息就会一直阻塞，对于rabbitmq-server来说，
		 * 他是没有超时存在的，即除非重启rabbitmq，否则该消息队列会一直阻塞，直到收到响应，但如果与该消息队列的subscirbe断开的话，
		 * 则表明过期，即该消息队列中消息会尝试重新发消息给一个订阅者进行处理
		 */
		channel.basicConsume(recieve.getQueueName(), false, consumer);
		//String message = null;
		while (true)
		{
			//nextDelivery是一个阻塞方法（内部实现其实是阻塞队列的take方法）
			QueueingConsumer.Delivery delivery = consumer.nextDelivery();
			//message = new String(delivery.getBody());
			Object o = SerializeUtil.deserializeObject(delivery.getBody(), recieve.getClass());
			if(o != null){
				if(recieve.excute(o) && !recieve.errorDestroy()){
					//logger.info("日志执行成功");
					//确认消息，已经收到  
					channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
				}else{
					//出错直接销毁
					channel.basicAck(delivery.getEnvelope().getDeliveryTag(), true);
				}
			}
				
		}
	}
}
