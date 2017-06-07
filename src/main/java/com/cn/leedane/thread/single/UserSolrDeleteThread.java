package com.cn.leedane.thread.single;

import com.cn.leedane.lucene.solr.UserSolrHandler;

/**
 * 用户solr删除索引异步任务
 * @author LeeDane
 * 2017年6月7日 上午9:55:29
 * version 1.0
 */
public class UserSolrDeleteThread implements Runnable{
	
	private String id;
	public UserSolrDeleteThread(String id) {
		this.id = id;
	}

	@Override
	public void run() {
		UserSolrHandler.getInstance().deleteBean(id);
	}

}
