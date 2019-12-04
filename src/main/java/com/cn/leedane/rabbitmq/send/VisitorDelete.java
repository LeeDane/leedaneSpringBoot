package com.cn.leedane.rabbitmq.send;

import com.cn.leedane.model.VisitorBean;
import com.cn.leedane.rabbitmq.recieve.VisitorDeleteRecieve;


/**
 * 删除访客的实现类
 * @author LeeDane
 * 2017年6月29日 下午4:28:49
 * version 1.0
 */
public class VisitorDelete implements ISend{
	
	private VisitorBean visitorBean;
	public VisitorDelete(VisitorBean visitorBean){
		this.visitorBean = visitorBean;
	}

	@Override
	public String getQueueName() {
		return VisitorDeleteRecieve.QUEUE_NAME;
	}

	@Override
	public Object getQueueObject() {
		return visitorBean;
	}

}
