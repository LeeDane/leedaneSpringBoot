package com.cn.leedane.rabbitmq.send;


/**
 * 发送的接口
 * @author LeeDane
`* 2016年8月5日 下午3:42:53
 * Version 1.0
 */
public interface ISend {
	/**
	 * 获取队列的名称
	 * @return
	 */
	public String getQueueName();
	
	/**
	 * 队列中对象
	 * @return
	 */
	public Object getQueueObject();
}
