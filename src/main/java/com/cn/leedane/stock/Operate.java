package com.cn.leedane.stock;

import java.util.List;

/**
 * 每一列的操作结果
 * @author LeeDane
 * 2018年7月10日 下午6:36:25
 * version 1.0
 */
public class Operate {

	//所有的组合数据
	private List<List<Point>> points;
	
	//运算结果， 运算类型，0：加法， 1减法： 2乘法 3、除法
	private List<ComputeResult> computeResults;
	
	//最终的得分
	private float score;
	
	//最终计算得分的描述信息
	private String scoreDesc;
	
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

	public float getScore() {
		return score;
	}

	public void setScore(float score) {
		this.score = score;
	}

	public String getScoreDesc() {
		return scoreDesc;
	}

	public void setScoreDesc(String scoreDesc) {
		this.scoreDesc = scoreDesc;
	}

	public List<ComputeResult> getComputeResults() {
		return computeResults;
	}

	public void setComputeResults(List<ComputeResult> computeResults) {
		this.computeResults = computeResults;
	}
}
