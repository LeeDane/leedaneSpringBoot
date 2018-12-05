package com.cn.leedane.thread.single;

import org.apache.log4j.Logger;

import com.cn.leedane.model.clock.ClockDynamicQueueBean;
import com.cn.leedane.rabbitmq.SendMessage;
import com.cn.leedane.rabbitmq.send.ClockDynamicSend;
import com.cn.leedane.rabbitmq.send.ISend;

/**
 * 任务动态处理异步任务
 * @author LeeDane
 * 2018年11月26日 下午3:51:07
 * version 1.0
 */
public class ClockDynamicThread implements Runnable{
	
	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(getClass());
	private ClockDynamicQueueBean mClockDynamicQueueBean;
	public ClockDynamicThread(ClockDynamicQueueBean clockDynamicQueueBean) {
		this.mClockDynamicQueueBean = clockDynamicQueueBean;
	}

	@Override
	public void run() {
		ISend send = new ClockDynamicSend(mClockDynamicQueueBean);
		SendMessage sendMessage = new SendMessage(send);
		sendMessage.sendMsg();//发送任务动态记录到消息队列
	}

}
