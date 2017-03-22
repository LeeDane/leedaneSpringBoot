package com.cn.leedane.rabbitmq.send;

import com.cn.leedane.model.EmailBean;
import com.cn.leedane.rabbitmq.recieve.EmailRecieve;


/**
 * 邮件发送的实现类
 * @author LeeDane
 * 2016年9月6日 上午9:54:57
 * Version 1.0
 */
public class EmailSend implements ISend{
	
	private EmailBean emailBean;
	public EmailSend(EmailBean emailBean){
		this.emailBean = emailBean;
	}

	@Override
	public String getQueueName() {
		return EmailRecieve.QUEUE_NAME;
	}

	@Override
	public Object getQueueObject() {
		return emailBean;
	}

}
