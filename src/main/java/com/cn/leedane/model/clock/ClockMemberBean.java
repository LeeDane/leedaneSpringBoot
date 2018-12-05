package com.cn.leedane.model.clock;

import com.cn.leedane.model.RecordTimeBean;
import com.cn.leedane.mybatis.table.annotation.Column;

/**
 * 任务成员实体bean
 * @author LeeDane
 * 2018年8月30日 上午11:51:58
 * version 1.0
 */
public class ClockMemberBean extends RecordTimeBean{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//任务的状态：0：禁用， 1：正常，2：删除，10: 等待成员同意加入， 11：等待管理同意加入， 12：管理员同意
	
	/**
	 * 任务成员id，必须字段
	 */
	@Column(value="member_id", required = true)
	private int memberId;
	
	/**
	 * 任务的ID，必须字段
	 */
	@Column(value="clock_id", required = true)
	private int clockId;
	
	/**
	 * 任务的提醒时间
	 */
	@Column(value="remind", required = true)
	private String remind; //提醒时间
	
	/**
	 * 是否接受该任务的相关消息
	 */
	@Column(value="notification", required = true)
	private boolean notification;
	
	/**
	 * 冗余用户表字段性别字段,不需要存在数据库中，只需要在缓存中同意存储即可
	 */
	@Column(required = false)
	private String sex;
	
	/**
	 * 冗余用户表年龄字段,不需要存在数据库中，只需要在缓存中同意存储即可
	 */
	@Column(required = false)
	private int age;

	public int getMemberId() {
		return memberId;
	}

	public void setMemberId(int memberId) {
		this.memberId = memberId;
	}

	public int getClockId() {
		return clockId;
	}

	public void setClockId(int clockId) {
		this.clockId = clockId;
	}

	public String getRemind() {
		return remind;
	}

	public void setRemind(String remind) {
		this.remind = remind;
	}

	public boolean isNotification() {
		return notification;
	}

	public void setNotification(boolean notification) {
		this.notification = notification;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

}
