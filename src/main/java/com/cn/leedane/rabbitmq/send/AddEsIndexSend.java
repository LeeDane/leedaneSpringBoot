package com.cn.leedane.rabbitmq.send;

import com.cn.leedane.rabbitmq.recieve.AddEsIndexRecieve;


/**
 * 加入Es索引发送的实现类
 * @author LeeDane
`* 2019年7月5日 下午18:44:15
 * Version 1.0
 */
public class AddEsIndexSend<T> implements ISend{

	private T object;
	public AddEsIndexSend(T object){
		this.object = object;
	}

	@Override
	public String getQueueName() {
		return AddEsIndexRecieve.QUEUE_NAME;
	}

	@Override
	public Object getQueueObject() {
		return object;
	}

}
