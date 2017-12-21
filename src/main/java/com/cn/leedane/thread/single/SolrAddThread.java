package com.cn.leedane.thread.single;

import com.cn.leedane.lucene.solr.BaseSolrHandler;
import com.cn.leedane.lucene.solr.BlogSolrHandler;
import com.cn.leedane.model.BlogBean;
import com.cn.leedane.model.IDBean;

/**
 * solr加入索引异步任务
 * @author LeeDane
 * 2017年6月6日 下午6:16:21
 * version 1.0
 */
public class SolrAddThread<T extends IDBean> implements Runnable{
	
	private Object bean;
	private BaseSolrHandler<T> handler;
	public SolrAddThread(BaseSolrHandler<T> handler, Object bean) {
		this.bean = bean;
		this.handler = handler;
	}
	

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		handler.addBean((T)bean);
	}

}
