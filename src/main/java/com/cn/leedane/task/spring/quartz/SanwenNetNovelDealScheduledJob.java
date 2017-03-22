package com.cn.leedane.task.spring.quartz;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.cn.leedane.task.spring.scheduling.SanwenNetNovelBean;

/**
 * 散文网短篇小说的调度任务
 * @author LeeDane
 * 2016年7月12日 下午3:23:27
 * Version 1.0
 */
public class SanwenNetNovelDealScheduledJob extends QuartzJobBean {

	private SanwenNetNovelBean sanwenNetNovelBean; 

	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {
		try {
			//sanwenNetNovelBean.deal();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setSanwenNetNovelBean(SanwenNetNovelBean sanwenNetNovelBean) {
		this.sanwenNetNovelBean = sanwenNetNovelBean;
	}
}
