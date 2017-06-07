package com.cn.leedane.thread.single;

import com.cn.leedane.lucene.solr.MoodSolrHandler;
import com.cn.leedane.model.MoodBean;

/**
 * 心情solr加入索引异步任务
 * @author LeeDane
 * 2017年6月7日 上午9:39:00
 * version 1.0
 */
public class MoodSolrAddThread implements Runnable{
	
	private MoodBean moodBean;
	public MoodSolrAddThread(MoodBean moodBean) {
		this.moodBean = moodBean;
	}

	@Override
	public void run() {
		MoodSolrHandler.getInstance().addBean(moodBean);
	}

}
