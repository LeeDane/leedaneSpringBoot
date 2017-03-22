package com.cn.leedane.rabbitmq.send;

import com.cn.leedane.model.FinancialReport;
import com.cn.leedane.rabbitmq.recieve.FinancialMonthReportRecieve;


/**
 * 记账月报的实现类
 * @author LeeDane
 * 2016年11月2日 上午11:29:34
 * Version 1.0
 */
public class FinancialMonthReportSend implements ISend{
	
	private FinancialReport financialReport;
	public FinancialMonthReportSend(FinancialReport financialReport){
		this.financialReport = financialReport;
	}

	@Override
	public String getQueueName() {
		return FinancialMonthReportRecieve.QUEUE_NAME;
	}

	@Override
	public Object getQueueObject() {
		return financialReport;
	}

}
