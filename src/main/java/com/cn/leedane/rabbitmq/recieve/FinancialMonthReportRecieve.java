package com.cn.leedane.rabbitmq.recieve;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.cn.leedane.handler.NotificationHandler;
import com.cn.leedane.model.FinancialReport;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.springboot.SpringUtil;
import com.cn.leedane.utils.EnumUtil.NotificationType;

/**
 * 记账月报消费者
 * @author LeeDane
 * 2016年11月2日 上午11:18:33
 * Version 1.0
 */
public class FinancialMonthReportRecieve implements IRecieve{

	private Logger logger = Logger.getLogger(getClass());
	
	public final static String QUEUE_NAME = "financial_month_rabbitmq";
	@Override
	public String getQueueName() {
		return QUEUE_NAME;
	}

	@Override
	public Class<?> getQueueClass() {
		return FinancialReport.class;
	}

	@Override
	public boolean excute(Object obj) {
		boolean success = false;
		try{
			if(obj == null)
				return success;
			
			FinancialReport financialReport = (FinancialReport)obj;
			NotificationHandler notificationHandler = (NotificationHandler) SpringUtil.getBean("notificationHandler");
			UserBean userBean = financialReport.getUser();
			notificationHandler.sendNotificationById(true, userBean, userBean.getId(), financialReport.getDesc(), NotificationType.通知, "t_financial_report", 1, financialReport);	
			logger.info(JSON.toJSON(financialReport));
			success = true;
		}catch(Exception e){
			logger.info(e.getMessage());
			e.printStackTrace();
		}
		return success;
	}

	@Override
	public boolean errorDestroy() {
		return false;//出错不能销毁
	}

}
