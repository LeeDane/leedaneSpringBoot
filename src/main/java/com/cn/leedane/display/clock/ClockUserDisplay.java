package com.cn.leedane.display.clock;

import java.io.Serializable;


/**
 * 任务提醒关联用户信息的展示类bean
 * @author LeeDane
 * 2018年8月30日 下午3:28:24
 * version 1.0
 */
public class ClockUserDisplay implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int id;
	
	private String name;
	
	private String picPath;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPicPath() {
		return picPath;
	}

	public void setPicPath(String picPath) {
		this.picPath = picPath;
	}
	
}
