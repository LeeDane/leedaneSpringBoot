package com.cn.leedane.thread.single;

import org.apache.log4j.Logger;

import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.VisitorBean;
import com.cn.leedane.rabbitmq.SendMessage;
import com.cn.leedane.rabbitmq.send.ISend;
import com.cn.leedane.rabbitmq.send.VisitorDelete;
import com.cn.leedane.utils.DateUtil;

/**
 * 删除访客记录异步任务
 * @author LeeDane
 * 2017年6月29日 下午4:28:00
 * version 1.0
 */
public class VisitorDeleteThread implements Runnable{
	private Logger logger = Logger.getLogger(getClass());
	private UserBean mUser;
	private String mTableName;
	private long mTableId;
	public VisitorDeleteThread(UserBean user, String tableName, long tableId) {
		this.mUser = user;
		this.mTableName = tableName;
		this.mTableId = tableId;
	}

	@Override
	public void run() {
		VisitorBean visitorBean = new VisitorBean();
		logger.info("VisitorServiceImpl-->deleteVisitor():tableName="+mTableName+", tableId="+mTableId);
		visitorBean.setCreateUserId(mUser.getId());
		visitorBean.setCreateTime(DateUtil.getCurrentTime());
		visitorBean.setTableName(mTableName);
		visitorBean.setTableId(mTableId);
		ISend send = new VisitorDelete(visitorBean);
		SendMessage sendMessage = new SendMessage(send);
		sendMessage.sendMsg();//发送访客记录到消息队列
	}

}
