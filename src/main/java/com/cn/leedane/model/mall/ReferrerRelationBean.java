package com.cn.leedane.model.mall;

import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.RecordTimeBean;
import com.cn.leedane.mybatis.table.annotation.Column;
import com.cn.leedane.mybatis.table.annotation.Table;

/**
 * 推荐关系实体bean
 * @author LeeDane
 * 2017年12月11日 下午3:31:21
 * version 1.0
 */
public class ReferrerRelationBean extends IDBean {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long id;

	private long parent;
	private String account;

	@Override
	public long getId() {
		return id;
	}

	@Override
	public void setId(long id) {
		this.id = id;
	}

	public long getParent() {
		return parent;
	}

	public void setParent(long parent) {
		this.parent = parent;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}
}
