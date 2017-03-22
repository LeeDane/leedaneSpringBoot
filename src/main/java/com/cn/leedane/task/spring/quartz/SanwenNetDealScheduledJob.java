package com.cn.leedane.task.spring.quartz;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.cn.leedane.task.spring.scheduling.SanwenNetBean;

/**
 * 散文网的调度任务
 * @author LeeDane
 * 2016年7月12日 下午3:23:12
 * Version 1.0
 */
public class SanwenNetDealScheduledJob extends QuartzJobBean {

	private SanwenNetBean sanwenNetBean; 

	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {
		try {
			//sanwenNetBean.deal();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setSanwenNetBean(SanwenNetBean sanwenNetBean) {
		this.sanwenNetBean = sanwenNetBean;
	}
}
