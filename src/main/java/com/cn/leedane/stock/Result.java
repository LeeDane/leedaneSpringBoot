package com.cn.leedane.stock;

import java.util.List;

/**
 * 最终结果的显示
 * @author LeeDane
 * 2018年7月11日 下午5:23:26
 * version 1.0
 */
public class Result {

	//一共四个
	private List<Operate> operates;
	
	//运算结果， 运算类型，0：加法， 1减法： 2乘法 3、除法
	private List<ComputeResult> computeResults;
	
	//最终的得分
	private float score;
	
	//最终计算得分的描述信息
	private String scoreDesc;
	
	/**
	 * 开奖结果
	 */
	private String number;
	
	/**
	 * 步数
	 */
	private int step;
	
	/**
	 * 深度
	 */
	private int deep;

	public List<Operate> getOperates() {
		return operates;
	}

	public void setOperates(List<Operate> operates) {
		this.operates = operates;
	}

	public List<ComputeResult> getComputeResults() {
		return computeResults;
	}

	public void setComputeResults(List<ComputeResult> computeResults) {
		this.computeResults = computeResults;
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

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public int getDeep() {
		return deep;
	}

	public void setDeep(int deep) {
		this.deep = deep;
	}
	
	
}
