package com.cn.leedane.task.spring.quartz;

import java.util.Date;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.cn.leedane.task.spring.scheduling.FinancialMonth;
import com.cn.leedane.utils.DateUtil;

/**
 * 记账月报的调度任务
 * @author LeeDane
 * 2016年11月2日 上午9:43:01
 * Version 1.0
 */
public class FinancialMonthScheduledJob extends QuartzJobBean {

	private FinancialMonth financialMonth; 

	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {
		try {
			financialMonth.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setFinancialMonth(FinancialMonth financialMonth) {
		this.financialMonth = financialMonth;
	}
}
