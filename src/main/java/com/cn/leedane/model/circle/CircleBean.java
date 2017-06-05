package com.cn.leedane.model.circle;

import org.apache.solr.client.solrj.beans.Field;

import com.cn.leedane.model.RecordTimeBean;
import com.cn.leedane.mybatis.table.annotation.Column;

/**
 * 圈子实体bean
 * @author LeeDane
 * 2017年5月30日 下午4:54:35
 * version 1.0
 */
public class CircleBean extends RecordTimeBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7320151621589479551L;
	
	//圈子的状态：0：禁用， 1：正常
	
	/**
	 * 圈子的名称
	 */
	private String name;
	
	/**
	 * 圈子的描述信息(介绍信息)
	 */
	private String describe;
	
	/**
	 * 圈子的描述信息(介绍信息)
	 */
	@Column(value = "order_www", required = false)
	@Field
	private String ffdd;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}

	public String getFfdd() {
		return ffdd;
	}

	public void setFfdd(String ffdd) {
		this.ffdd = ffdd;
	}
	
	
}
