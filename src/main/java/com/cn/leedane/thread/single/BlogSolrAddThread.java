package com.cn.leedane.thread.single;

import com.cn.leedane.lucene.solr.BlogSolrHandler;
import com.cn.leedane.model.BlogBean;

/**
 * 文章solr加入索引异步任务
 * @author LeeDane
 * 2017年6月6日 下午6:16:21
 * version 1.0
 */
public class BlogSolrAddThread implements Runnable{
	
	private BlogBean blogBean;
	public BlogSolrAddThread(BlogBean blogBean) {
		this.blogBean = blogBean;
	}

	@Override
	public void run() {
		BlogSolrHandler.getInstance().addBean(blogBean);
	}

}
