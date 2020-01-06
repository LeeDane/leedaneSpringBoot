package com.cn.leedane.model.util;

import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.RecordTimeBean;
import com.cn.leedane.mybatis.table.annotation.Column;
import org.apache.solr.client.solrj.beans.Field;

/**
 * 省实体bean
 * @author LeeDane
 * 2017年12月11日 下午3:31:21
 * version 1.0
 */
public class ProvinceBean extends RecordTimeBean {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 省的名称
	 */
	@Column(value="name")
	private String name;

	/**
	 * 省的编码
	 */
	@Column(value="code")
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
