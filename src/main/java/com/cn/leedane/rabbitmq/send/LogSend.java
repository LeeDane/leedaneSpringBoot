package com.cn.leedane.rabbitmq.send;

import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.rabbitmq.recieve.LogRecieve;


/**
 * 日志发送的实现类
 * @author LeeDane
`* 2016年8月5日 下午3:44:15
 * Version 1.0
 */
public class LogSend implements ISend{
	
	private OperateLogBean operateLogBean;
	public LogSend(OperateLogBean operateLogBean){
		this.operateLogBean = operateLogBean;
	}

	@Override
	public String getQueueName() {
		return LogRecieve.QUEUE_NAME;
	}

	@Override
	public Object getQueueObject() {
		return operateLogBean;
	}

}
