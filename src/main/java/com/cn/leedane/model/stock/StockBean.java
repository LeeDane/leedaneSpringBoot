package com.cn.leedane.model.stock;

import org.apache.solr.client.solrj.beans.Field;

import com.cn.leedane.model.RecordTimeBean;

/**
 * 股票实体bean
 * @author LeeDane
 * 2018年6月21日 上午10:49:57
 * version 1.0
 */
public class StockBean extends RecordTimeBean{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//股票的状态：0：禁用， 1：正常
	
	/**
	 * 股票的名称
	 */
	@Field
	private String name;
	
	/**
	 * 股票编码
	 */
	@Field
	private String code;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
