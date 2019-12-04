package com.cn.leedane.thread.single;

import org.apache.log4j.Logger;

import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.clock.ClockScoreQueueBean;
import com.cn.leedane.rabbitmq.SendMessage;
import com.cn.leedane.rabbitmq.send.ClockScoreSend;
import com.cn.leedane.rabbitmq.send.ISend;

/**
 * 任务积分处理异步任务
 * @author LeeDane
 * 2018年11月5日 下午5:02:44
 * version 1.0
 */
public class ClockScoreThread implements Runnable{
	private Logger logger = Logger.getLogger(getClass());
	private UserBean mUser;
	private ClockScoreQueueBean mClockScoreQueueBean;
	public ClockScoreThread(UserBean user, ClockScoreQueueBean clockScoreQueueBean) {
		this.mUser = user;
		this.mClockScoreQueueBean = clockScoreQueueBean;
	}

	@Override
	public void run() {
		logger.info("ClockScoreThread-->run():mOperateType="+mClockScoreQueueBean.getOperateType()+", clockId="+mClockScoreQueueBean.getClockScoreBean().getClockId());
//		mClockScoreQueueBean.setCreateUserId(mUser.getId());
//		mClockScoreQueueBean.setCreateTime(DateUtil.getCurrentTime());
//		mClockScoreQueueBean.setOperateType(mOperateType);
		ISend send = new ClockScoreSend(mClockScoreQueueBean);
		SendMessage sendMessage = new SendMessage(send);
		sendMessage.sendMsg();//发送访客记录到消息队列
	}

}
