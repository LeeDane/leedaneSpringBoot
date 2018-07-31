package com.cn.leedane.stock;

import java.util.List;

/**
 * 每一列的组合(包含)
 * @author LeeDane
 * 2018年7月10日 下午2:25:01
 * version 1.0
 */
public class Combines {

	//所有的组合数据
	private List<List<Point>> points;
	
	//每次移动的步数
	private int mobile;

	public List<List<Point>> getPoints() {
		return points;
	}

	public void setPoints(List<List<Point>> points) {
		this.points = points;
	}

	public int getMobile() {
		return mobile;
	}

	public void setMobile(int mobile) {
		this.mobile = mobile;
	} 
}
