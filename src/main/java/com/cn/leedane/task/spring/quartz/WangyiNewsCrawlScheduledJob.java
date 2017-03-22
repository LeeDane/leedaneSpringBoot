package com.cn.leedane.task.spring.quartz;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.cn.leedane.task.spring.scheduling.WangyiNewsBean;

/**
 * 网易新闻的抓取调度任务
 * @author LeeDane
 * 2016年7月12日 下午3:23:52
 * Version 1.0
 */
public class WangyiNewsCrawlScheduledJob extends QuartzJobBean {

	private WangyiNewsBean wangyiNewsBean; 

	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {
		try {
			//wangyiNewsBean.crawl();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setWangyiNewsBean(WangyiNewsBean wangyiNewsBean) {
		this.wangyiNewsBean = wangyiNewsBean;
	}
}
