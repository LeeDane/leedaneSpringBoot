package com.cn.leedane.model.circle;

import org.apache.solr.client.solrj.beans.Field;

import com.cn.leedane.model.RecordTimeBean;
import com.cn.leedane.mybatis.table.annotation.Column;

/**
 * 圈子创建的要求bean
 * @author LeeDane
 * 2017年6月11日 下午4:29:44
 * version 1.0
 */
public class CircleCreateLimitBean extends RecordTimeBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7320151621589479551L;
	
	/**
	 * 用户积分大于等于这个数
	 */
	@Column("left_score")
	@Field
	private int leftScore = 0;
	
	/**
	 * 用户积分小于这个数
	 */
	@Column("right_score")
	@Field
	private int rightScore = 0;
	
	/**
	 * 用户积分在这个范围可以创建的圈子数
	 */
	@Column("number")
	@Field
	private int number = 0;
	
	/**
	 * 描述信息
	 */
	@Column("limit_desc")
	@Field
	private String limitDesc;

	public int getLeftScore() {
		return leftScore;
	}

	public void setLeftScore(int leftScore) {
		this.leftScore = leftScore;
	}

	public int getRightScore() {
		return rightScore;
	}

	public void setRightScore(int rightScore) {
		this.rightScore = rightScore;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getLimitDesc() {
		return limitDesc;
	}

	public void setLimitDesc(String limitDesc) {
		this.limitDesc = limitDesc;
	}
}
