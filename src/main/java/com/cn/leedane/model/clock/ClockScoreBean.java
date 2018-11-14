package com.cn.leedane.model.clock;

import java.util.Date;

import com.cn.leedane.model.RecordTimeBean;
import com.cn.leedane.mybatis.table.annotation.Column;


/**
 * 任务积分实体类
 * @author LeeDane
 * 2018年11月5日 下午4:46:31
 * version 1.0
 */
public class ClockScoreBean extends RecordTimeBean{
	
	private static final long serialVersionUID = 1L;
	//积分的状态,1：正常，0:禁用，2、删除，3、审核中， 4、审核不通过
	
	/**
	 * 当前的获得的积分(非总积分)
	 */
	private int score;
	
	/**
	 * 任务的积分日期
	 */
	@Column(value="score_date", required = true)
	private Date scoreDate;
	
	/**
	 * 历史的总积分(冗余字段)
	 */
	@Column(value="total_score", required = true)
	private int totalScore;
	/**
	 * 描述 信息
	 */
	@Column(value="score_desc", required = true)
	private String scoreDesc;
	
	/**
	 * 相关联的任务ID
	 */
	@Column(value="clock_id", required = true)
	private int clockId;
	
	/**
	 * 相关联的任务业务类型
	 */
	@Column(value="business_type", required = true)
	private int businessType;

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getTotalScore() {
		return totalScore;
	}

	public void setTotalScore(int totalScore) {
		this.totalScore = totalScore;
	}

	public String getScoreDesc() {
		return scoreDesc;
	}

	public void setScoreDesc(String scoreDesc) {
		this.scoreDesc = scoreDesc;
	}

	public int getClockId() {
		return clockId;
	}

	public void setClockId(int clockId) {
		this.clockId = clockId;
	}

	public int getBusinessType() {
		return businessType;
	}

	public void setBusinessType(int businessType) {
		this.businessType = businessType;
	}

	public Date getScoreDate() {
		return scoreDate;
	}

	public void setScoreDate(Date scoreDate) {
		this.scoreDate = scoreDate;
	}
	
	
}
