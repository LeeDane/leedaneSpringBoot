package com.cn.leedane.thread.single;

import org.apache.log4j.Logger;

import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.VisitorBean;
import com.cn.leedane.rabbitmq.SendMessage;
import com.cn.leedane.rabbitmq.send.ISend;
import com.cn.leedane.rabbitmq.send.VisitorSend;
import com.cn.leedane.utils.DateUtil;

/**
 * 保存访客记录异步任务
 * @author LeeDane
 * 2017年6月22日 上午11:15:10
 * version 1.0
 */
public class VisitorSaveThread implements Runnable{
	private Logger logger = Logger.getLogger(getClass());
	private UserBean mUser;
	private String mFroms;
	private String mTableName;
	private long mTableId;
	private int mStatus;
	public VisitorSaveThread(UserBean user, String froms, String tableName, long tableId, int status) {
		this.mUser = user;
		this.mFroms = froms;
		this.mTableName = tableName;
		this.mTableId = tableId;
		this.mStatus = status;
	}

	@Override
	public void run() {
		VisitorBean visitorBean = new VisitorBean();
		logger.info("VisitorServiceImpl-->saveVisitor():tableName="+mTableName+", tableId="+mTableId+",status="+mStatus);
		visitorBean.setStatus(mStatus);
		visitorBean.setCreateUserId(mUser.getId());
		visitorBean.setCreateTime(DateUtil.getCurrentTime());
		visitorBean.setTableName(mTableName);
		visitorBean.setTableId(mTableId);
		visitorBean.setFroms(mFroms);
		ISend send = new VisitorSend(visitorBean);
		SendMessage sendMessage = new SendMessage(send);
		sendMessage.sendMsg();//发送访客记录到消息队列
	}

}
