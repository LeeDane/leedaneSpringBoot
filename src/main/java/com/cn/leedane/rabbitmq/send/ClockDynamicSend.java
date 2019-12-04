package com.cn.leedane.rabbitmq.send;

import com.cn.leedane.model.clock.ClockDynamicQueueBean;
import com.cn.leedane.rabbitmq.recieve.ClockDynamicRecieve;


/**
 * 任务动态处理
 * @author LeeDane
 * 2018年11月26日 下午3:57:52
 * version 1.0
 */
public class ClockDynamicSend implements ISend{
	
	private ClockDynamicQueueBean mClockDynamicQueueBean;
	public ClockDynamicSend(ClockDynamicQueueBean clockDynamicQueueBean){
		this.mClockDynamicQueueBean = clockDynamicQueueBean;
	}

	@Override
	public String getQueueName() {
		return ClockDynamicRecieve.QUEUE_NAME;
	}

	@Override
	public Object getQueueObject() {
		return mClockDynamicQueueBean;
	}

}
