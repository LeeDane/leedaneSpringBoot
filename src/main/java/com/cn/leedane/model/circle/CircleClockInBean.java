package com.cn.leedane.model.circle;

import org.apache.solr.client.solrj.beans.Field;

import com.cn.leedane.model.RecordTimeBean;
import com.cn.leedane.mybatis.table.annotation.Column;

/**
 * 打卡的实体bean
 * @author LeeDane
 * 2017年6月14日 下午4:02:46
 * version 1.0
 */
public class CircleClockInBean extends RecordTimeBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8962183001433215447L;
	
	/**
	 * 圈子的id,外键
	 */
	@Column("circle_id")
	@Field
	private int circleId;

	/**
	 * 上次记录的id
	 */
	private int pid;  
	
	/**
	 * 连续签到的天数
	 */
	private int continuous;
	
	/**
	 * 签到方式
	 */
	private String froms;
	
	/**
	 * 创建日期(用于和create_user_id做联合约束记录的唯一性)
	 */
	@Column("create_date")
	@Field
	private String createDate;

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public int getContinuous() {
		return continuous;
	}

	public void setContinuous(int continuous) {
		this.continuous = continuous;
	}

	public String getFroms() {
		return froms;
	}

	public void setFroms(String froms) {
		this.froms = froms;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public int getCircleId() {
		return circleId;
	}

	public void setCircleId(int circleId) {
		this.circleId = circleId;
	}
	
	
}
