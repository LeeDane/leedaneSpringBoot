package com.cn.leedane.thread.single;

import com.cn.leedane.lucene.solr.BaseSolrHandler;
import com.cn.leedane.model.IDBean;

/**
 * solr删除索引异步任务
 * @author LeeDane
 * 2017年11月15日 下午4:11:27
 * version 1.0
 */
public class SolrDeleteThread<T extends IDBean> implements Runnable{
	
	private String id;
	private BaseSolrHandler<T> handler;
	public SolrDeleteThread(BaseSolrHandler<T> handler, String id) {
		this.id = id;
		this.handler = handler;
	}

	@Override
	public void run() {
		handler.deleteBean(id);
	}

}
