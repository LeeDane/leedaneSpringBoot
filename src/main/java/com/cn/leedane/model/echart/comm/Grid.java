package com.cn.leedane.model.echart.comm;

import java.io.Serializable;

/**
 * 
 * @author LeeDane
 * 2017年12月24日 下午4:09:01
 * version 1.0
 */
public class Grid implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String left;
	private String right;
	private String bottom;
	private boolean containLabel;
	public String getLeft() {
		return left;
	}
	public void setLeft(String left) {
		this.left = left;
	}
	public String getRight() {
		return right;
	}
	public void setRight(String right) {
		this.right = right;
	}
	public String getBottom() {
		return bottom;
	}
	public void setBottom(String bottom) {
		this.bottom = bottom;
	}
	public boolean isContainLabel() {
		return containLabel;
	}
	public void setContainLabel(boolean containLabel) {
		this.containLabel = containLabel;
	}
}
