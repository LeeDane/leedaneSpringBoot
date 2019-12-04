package com.cn.leedane.model.echart.comm;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author LeeDane
 * 2017年12月24日 下午4:07:47
 * version 1.0
 */
public class Legend implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<String> data;

	public List<String> getData() {
		return data;
	}

	public void setData(List<String> data) {
		this.data = data;
	}
}
