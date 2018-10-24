package com.cn.leedane.model.clock;

import java.util.Date;

import com.cn.leedane.model.RecordTimeBean;
import com.cn.leedane.mybatis.table.annotation.Column;

/**
 * 任务打卡实体bean
 * @author LeeDane
 * 2018年8月30日 下午4:46:42
 * version 1.0
 */
public class ClockInBean extends RecordTimeBean{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//任务的状态：0：禁用， 1：正常，2：删除，9：结束
	
	/**
	 * 任务的ID，必须字段
	 */
	@Column(value="clock_id", required = true)
	private int clockId;
	
	/**
	 * 任务的方式
	 */
	@Column(value="froms", required = true)
	private String froms;
	
	/**
	 * 任务的打卡图片，当任务type为2的时候是必须字段
	 */
	@Column(value="img", required = true)
	private String img;
	
	/**
	 * 任务的打卡位置，当任务type为3的时候是必须字段
	 */
	@Column(value="location", required = true)
	private String location;
	
	/**
	 * 任务的计步打卡，当任务type为4的时候是必须字段
	 */
	@Column(value="step", required = true)
	private int step;
	
	/**
	 * 任务的日期
	 */
	@Column(value="clock_date", required = true)
	private Date clockDate;

	public int getClockId() {
		return clockId;
	}

	public void setClockId(int clockId) {
		this.clockId = clockId;
	}

	public String getFroms() {
		return froms;
	}

	public void setFroms(String froms) {
		this.froms = froms;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public Date getClockDate() {
		return clockDate;
	}

	public void setClockDate(Date clockDate) {
		this.clockDate = clockDate;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}
	
}
