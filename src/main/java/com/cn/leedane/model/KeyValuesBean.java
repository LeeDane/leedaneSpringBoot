package com.cn.leedane.model;

import java.io.Serializable;
import java.util.List;


/**
 * key-value实体类
 * @author LeeDane
 * 2017年7月4日 上午10:37:38
 * Version 1.0
 */
public class KeyValuesBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	private List<KeyValueBean> data;


	public List<KeyValueBean> getData() {
		return data;
	}


	public void setData(List<KeyValueBean> data) {
		this.data = data;
	}
}
