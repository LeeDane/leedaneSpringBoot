package com.cn.leedane.model.echart.comm;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author LeeDane
 * 2017年12月24日 下午4:16:14
 * version 1.0
 */
public class Series implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private String type;
	private String stack;
	private List<Integer> data;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getStack() {
		return stack;
	}
	public void setStack(String stack) {
		this.stack = stack;
	}
	public List<Integer> getData() {
		return data;
	}
	public void setData(List<Integer> data) {
		this.data = data;
	}

}
