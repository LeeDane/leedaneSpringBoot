package com.cn.leedane.rabbitmq.send;

import com.cn.leedane.model.VisitorBean;
import com.cn.leedane.rabbitmq.recieve.VisitorRecieve;


/**
 * 访客的实现类
 * @author LeeDane
 * 2017年5月11日 下午5:23:02
 * version 1.0
 */
public class VisitorSend implements ISend{
	
	private VisitorBean visitorBean;
	public VisitorSend(VisitorBean visitorBean){
		this.visitorBean = visitorBean;
	}

	@Override
	public String getQueueName() {
		return VisitorRecieve.QUEUE_NAME;
	}

	@Override
	public Object getQueueObject() {
		return visitorBean;
	}

}
