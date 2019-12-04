package com.cn.leedane.model.echart.comm;

import java.io.Serializable;

/**
 * 
 * @author LeeDane
 * 2017年12月24日 下午5:28:27
 * version 1.0
 */
public class AxisLabel implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String formatter;

	public String getFormatter() {
		return formatter;
	}

	public void setFormatter(String formatter) {
		this.formatter = formatter;
	}
}
