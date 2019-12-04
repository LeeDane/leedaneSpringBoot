package com.cn.leedane.model.echart.comm;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author LeeDane
 * 2017年12月24日 下午4:11:25
 * version 1.0
 */
public class XAxis implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String type;
	
	private boolean boundaryGap;
	
	private List<String> data;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isBoundaryGap() {
		return boundaryGap;
	}

	public void setBoundaryGap(boolean boundaryGap) {
		this.boundaryGap = boundaryGap;
	}

	public List<String> getData() {
		return data;
	}

	public void setData(List<String> data) {
		this.data = data;
	}
}
