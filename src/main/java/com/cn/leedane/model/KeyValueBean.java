package com.cn.leedane.model;

import java.io.Serializable;


/**
 * key-value实体类
 * @author LeeDane
 * 2017年7月4日 上午10:37:38
 * Version 1.0
 */
public class KeyValueBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	private String key;
	
	private String value;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
