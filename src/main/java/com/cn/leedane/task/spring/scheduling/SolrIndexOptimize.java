package com.cn.leedane.task.spring.scheduling;

import org.apache.log4j.Logger;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Component;

import com.cn.leedane.lucene.solr.BlogSolrHandler;
import com.cn.leedane.lucene.solr.MoodSolrHandler;
import com.cn.leedane.lucene.solr.UserSolrHandler;

/**
 * 定时对solr索引优化任务
 * @author LeeDane
 * 2017年6月6日 下午5:45:50
 * version 1.0
 */
@Component("solrIndexOptimize")
public class SolrIndexOptimize extends AbstractScheduling{
	private Logger logger = Logger.getLogger(getClass());
	
	@Override
	public void execute() throws SchedulerException {
		logger.info("SolrIndexOptimize 开始对solr进行优化");
		BlogSolrHandler.getInstance().optimize(); //优化文章
		MoodSolrHandler.getInstance().optimize(); //优化心情
		UserSolrHandler.getInstance().optimize(); //优化用户
		logger.info("SolrIndexOptimize 本次solr优化完成");
			
	}
	
}
