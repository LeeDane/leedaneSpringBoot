package com.cn.leedane.task.spring.quartz;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.cn.leedane.task.spring.scheduling.SolrIndex;

/**
 * solr索引的调度任务
 * @author LeeDane
 * 2016年7月12日 下午3:23:35
 * Version 1.0
 */
public class SolrIndexScheduledJob extends QuartzJobBean {

	private SolrIndex solrIndex; 

	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {
		try {
			//solrIndex.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setSolrIndex(SolrIndex solrIndex) {
		this.solrIndex = solrIndex;
	}
}
