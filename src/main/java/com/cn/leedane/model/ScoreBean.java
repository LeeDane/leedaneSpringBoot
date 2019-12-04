package com.cn.leedane.model;


/**
 * 积分实体类
 * @author LeeDane
 * 2016年7月12日 上午10:53:17
 * Version 1.0
 */
//@Table(name="T_SCORE")
public class ScoreBean extends RecordTimeBean{
	
	private static final long serialVersionUID = 1L;
	//积分的状态,1：正常，0:禁用，2、删除，3、审核中， 4、审核不通过
	
	/**
	 * 当前的获得的积分(非总积分)
	 */
	private int score;
	
	/**
	 * 历史的总积分(冗余字段)
	 */
	private int totalScore;
	/**
	 * 描述 信息
	 */
	private String scoreDesc;
	
	/**
	 * 相关联的表名
	 */
	private String tableName;
	/**
	 * 相关联的表ID
	 */
	private long tableId;
	
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	
	//@Column(name="total_score", nullable=true)
	public int getTotalScore() {
		return totalScore;
	}
	public void setTotalScore(int totalScore) {
		this.totalScore = totalScore;
	}
	
	//@Column(name="score_desc")
	public String getScoreDesc() {
		return scoreDesc;
	}
	public void setScoreDesc(String scoreDesc) {
		this.scoreDesc = scoreDesc;
	}
	
	//@Column(name="table_name", length= 15, nullable=false)
	public String getTableName() {
		return tableName;
	}
	
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	//@Column(name="table_id", nullable = false)
	public long getTableId() {
		return tableId;
	}
	public void setTableId(long tableId) {
		this.tableId = tableId;
	}
	
}
