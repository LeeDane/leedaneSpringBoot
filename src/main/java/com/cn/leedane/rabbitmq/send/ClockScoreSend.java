package com.cn.leedane.rabbitmq.send;

import com.cn.leedane.model.clock.ClockScoreQueueBean;
import com.cn.leedane.rabbitmq.recieve.ClockScoreRecieve;


/**
 * 任务积分处理
 * @author LeeDane
 * 2017年6月29日 下午4:28:49
 * version 1.0
 */
public class ClockScoreSend implements ISend{
	
	private ClockScoreQueueBean clockScoreQueueBean;
	public ClockScoreSend(ClockScoreQueueBean clockScoreQueueBean){
		this.clockScoreQueueBean = clockScoreQueueBean;
	}

	@Override
	public String getQueueName() {
		return ClockScoreRecieve.QUEUE_NAME;
	}

	@Override
	public Object getQueueObject() {
		return clockScoreQueueBean;
	}

}
