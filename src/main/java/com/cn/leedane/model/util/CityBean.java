package com.cn.leedane.model.util;

import com.cn.leedane.model.RecordTimeBean;
import com.cn.leedane.mybatis.table.annotation.Column;

/**
 * 省实体bean
 * @author LeeDane
 * 2017年12月11日 下午3:31:21
 * version 1.0
 */
public class CityBean extends RecordTimeBean {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * 省的编码
	 */
	@Column(value="pcode")
	private String pcode;

	/**
	 * 市的名称
	 */
	@Column(value="name")
	private String name;

	/**
	 * 市的编码
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

	public String getPcode() {
		return pcode;
	}

	public void setPcode(String pcode) {
		this.pcode = pcode;
	}
}
