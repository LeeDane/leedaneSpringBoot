package com.cn.leedane.rabbitmq.send;

import com.cn.leedane.model.BlogBean;
import com.cn.leedane.model.MoodBean;
import com.cn.leedane.model.ReadBean;
import com.cn.leedane.rabbitmq.recieve.AddReadRecieve;


/**
 * 更新阅读数量的实现类
 * @author LeeDane
 * 2016年8月9日 上午11:13:02
 * Version 1.0
 */
public class AddReadSend implements ISend{
			
	private Object object;
	
	public AddReadSend(ReadBean readBean){
		this.object = readBean;
	}

	@Override
	public String getQueueName() {
		return AddReadRecieve.QUEUE_NAME;
	}

	@Override
	public Object getQueueObject() {
		return object;
	}
}
