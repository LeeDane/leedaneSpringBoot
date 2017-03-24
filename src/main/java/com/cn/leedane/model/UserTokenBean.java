package com.cn.leedane.model;
import java.util.Date;

import org.apache.solr.client.solrj.beans.Field;

import com.cn.leedane.mybatis.table.annotation.Column;

/**
 * APP用户所对象的token实体bean
 * @author LeeDane
 * 2017年3月24日 下午1:08:11
 * version 1.0
 */
public class UserTokenBean extends StatusBean{
	
	private static final long serialVersionUID = 1L;
	//用户状态    0:被禁止使用 1：正常，8、注册未激活,6：未完善信息 ， 4：被禁言 ，2:注销
	
	
	//维护的token，由于串很长，用text保存
	private String token;
	
	//过期时间
	private Date overdue;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Date getOverdue() {
		return overdue;
	}

	public void setOverdue(Date overdue) {
		this.overdue = overdue;
	}
}