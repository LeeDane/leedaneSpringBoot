package com.cn.leedane.thread.single;

import com.cn.leedane.model.BlogBean;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.rabbitmq.SendMessage;
import com.cn.leedane.rabbitmq.send.AddEsIndexSend;
import com.cn.leedane.rabbitmq.send.ISend;
import org.springframework.stereotype.Component;

/**
 * 加入es索引异步任务
 * @author LeeDane
 * 2017年6月6日 下午6:16:21
 * version 1.0
 */
public class EsIndexAddThread<T extends IDBean> implements Runnable{

	private Object bean;
	public EsIndexAddThread(Object bean) {
		this.bean = bean;
	}

	@Override
	public void run() {
		ISend send = new AddEsIndexSend(bean);
		SendMessage sendMessage = new SendMessage(send);
		sendMessage.sendMsg();
	}
}
