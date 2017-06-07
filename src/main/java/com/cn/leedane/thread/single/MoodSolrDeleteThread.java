package com.cn.leedane.thread.single;

import com.cn.leedane.lucene.solr.MoodSolrHandler;

/**
 * 心情solr删除索引异步任务
 * @author LeeDane
 * 2017年6月7日 上午9:43:15
 * version 1.0
 */
public class MoodSolrDeleteThread implements Runnable{
	
	private String id;
	public MoodSolrDeleteThread(String id) {
		this.id = id;
	}

	@Override
	public void run() {
		MoodSolrHandler.getInstance().deleteBean(id);
	}

}
