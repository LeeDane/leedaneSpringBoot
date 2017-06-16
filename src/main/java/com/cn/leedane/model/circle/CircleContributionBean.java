package com.cn.leedane.model.circle;

import org.apache.solr.client.solrj.beans.Field;

import com.cn.leedane.model.RecordTimeBean;
import com.cn.leedane.mybatis.table.annotation.Column;


/**
 * 贡献值实体类
 * @author LeeDane
 * 2017年6月14日 下午5:17:24
 * version 1.0
 */
//@Table(name="T_CIRCLE_CONTRIBUTION")
public class CircleContributionBean extends RecordTimeBean{
	
	private static final long serialVersionUID = 1L;
	//贡献值的状态,1：正常，0:禁用，2、删除，3、审核中， 4、审核不通过
	/**
	 * 圈子的id,外键
	 */
	@Column("circle_id")
	@Field
	private int circleId;
	
	/**
	 * 当前的获得的贡献值(非总贡献值)
	 */
	private int score;
	
	/**
	 * 历史的总贡献值(冗余字段)
	 */
	@Column("total_score")
	@Field
	private int totalScore;
	/**
	 * 描述 信息
	 */
	@Column("score_desc")
	@Field
	private String scoreDesc;
	
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
	public int getCircleId() {
		return circleId;
	}
	public void setCircleId(int circleId) {
		this.circleId = circleId;
	}
	
	
}
