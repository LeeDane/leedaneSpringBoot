package com.cn.leedane.model.echart.comm;

import java.io.Serializable;

/**
 * 工具栏
 * @author LeeDane
 * 2017年12月24日 下午4:05:32
 * version 1.0
 */
public class Tooltip implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String trigger;

	public String getTrigger() {
		return trigger;
	}

	public void setTrigger(String trigger) {
		this.trigger = trigger;
	}
}
