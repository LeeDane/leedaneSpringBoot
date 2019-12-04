package com.cn.leedane.model.clock;

import com.cn.leedane.model.RecordTimeBean;
import com.cn.leedane.mybatis.table.annotation.Column;

/**
 * 任务处理实体bean
 * @author LeeDane
 * 2018年10月23日 下午2:04:59
 * version 1.0
 */
public class ClockDealBean extends RecordTimeBean{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//任务的状态：0：禁用， 1：正常，2：删除，10: 等待成员同意加入， 11：等待管理同意加入， 12：管理员同意
	
	/**
	 * 任务成员id，必须字段
	 */
	@Column(value="member_id", required = true)
	private long memberId;
	
	/**
	 * 任务的ID，必须字段
	 */
	@Column(value="clock_id", required = true)
	private long clockId;
	
	/**
	 * 任务的新状态，对方处理后的状态
	 */
	@Column(value="new_status", required = true)
	private int newStatus;

	public long getMemberId() {
		return memberId;
	}

	public void setMemberId(long memberId) {
		this.memberId = memberId;
	}

	public long getClockId() {
		return clockId;
	}

	public void setClockId(long clockId) {
		this.clockId = clockId;
	}

	public int getNewStatus() {
		return newStatus;
	}

	public void setNewStatus(int newStatus) {
		this.newStatus = newStatus;
	}
}
