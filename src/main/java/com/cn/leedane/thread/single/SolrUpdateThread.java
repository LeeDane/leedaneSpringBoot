package com.cn.leedane.thread.single;

import com.cn.leedane.lucene.solr.BaseSolrHandler;
import com.cn.leedane.model.IDBean;

/**
 * solr修改索引异步任务
 * @author LeeDane
 * 2017年11月15日 下午4:13:02
 * version 1.0
 */
public class SolrUpdateThread<T extends IDBean> implements Runnable{
	
	private Object bean;
	private BaseSolrHandler<T> handler;
	
	public SolrUpdateThread(BaseSolrHandler<T> handler, Object bean) {
		this.bean = bean;
		this.handler = handler;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		handler.updateBean((T)bean);
	}

}
