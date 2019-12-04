package com.cn.leedane.model.echart.comm;

import java.io.Serializable;

/**
 * 
 * @author LeeDane
 * 2017年12月24日 下午4:13:16
 * version 1.0
 */
public class YAxis implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String type;
	
	private AxisLabel axisLabel;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public AxisLabel getAxisLabel() {
		return axisLabel;
	}

	public void setAxisLabel(AxisLabel axisLabel) {
		this.axisLabel = axisLabel;
	}
}
