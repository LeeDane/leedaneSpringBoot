package com.cn.leedane.thread.single;

import com.cn.leedane.lucene.solr.UserSolrHandler;
import com.cn.leedane.model.UserBean;

/**
 * 用户solr添加索引异步任务
 * @author LeeDane
 * 2017年6月7日 上午9:47:11
 * version 1.0
 */
public class UserSolrAddThread implements Runnable{
	
	private UserBean userBean;
	public UserSolrAddThread(UserBean userBean) {
		this.userBean = userBean;
	}

	@Override
	public void run() {
		UserSolrHandler.getInstance().addBean(userBean);
	}

}
