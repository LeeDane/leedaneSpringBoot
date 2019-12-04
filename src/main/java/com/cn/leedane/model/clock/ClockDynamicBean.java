package com.cn.leedane.model.clock;

import com.cn.leedane.model.RecordTimeBean;
import com.cn.leedane.mybatis.table.annotation.Column;


/**
 * 任务动态实体类
 * @author LeeDane
 * 2018年11月23日 下午4:21:31
 * version 1.0
 */
public class ClockDynamicBean extends RecordTimeBean{
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * 描述 信息
	 */
	@Column(value="dynamic_desc", required = true)
	private String dynamicDesc;
	
	/**
	 * 相关联的任务ID
	 */
	@Column(value="clock_id", required = true)
	private long clockId;

	/**
	 * 标记的消息类型，如任务打卡、其他等
	 */
	@Column(value="message_type", required = true)
	private int messageType;

	/**
	 * 标记该动态的等级,是否是公开的，默认是false
	 */
	@Column(value="publicity", required = true)
	private boolean publicity;
	
	public String getDynamicDesc() {
		return dynamicDesc;
	}

	public void setDynamicDesc(String dynamicDesc) {
		this.dynamicDesc = dynamicDesc;
	}

	public long getClockId() {
		return clockId;
	}

	public void setClockId(long clockId) {
		this.clockId = clockId;
	}

	public boolean isPublicity() {
		return publicity;
	}

	public void setPublicity(boolean publicity) {
		this.publicity = publicity;
	}

	public int getMessageType() {
		return messageType;
	}

	public void setMessageType(int messageType) {
		this.messageType = messageType;
	}
}
