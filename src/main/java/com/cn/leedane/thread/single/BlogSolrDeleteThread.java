package com.cn.leedane.thread.single;

import com.cn.leedane.lucene.solr.BlogSolrHandler;

/**
 * 文章solr删除索引异步任务
 * @author LeeDane
 * 2017年6月6日 下午6:16:21
 * version 1.0
 */
public class BlogSolrDeleteThread implements Runnable{
	
	private String id;
	public BlogSolrDeleteThread(String id) {
		this.id = id;
	}

	@Override
	public void run() {
		BlogSolrHandler.getInstance().deleteBean(id);
	}

}
