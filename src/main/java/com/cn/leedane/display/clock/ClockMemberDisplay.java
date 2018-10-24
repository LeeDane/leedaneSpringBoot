package com.cn.leedane.display.clock;

import java.io.Serializable;

/**
 * 任务成员展示类bean
 * @author LeeDane
 * 2018年10月19日 下午3:55:16
 * version 1.0
 */
public class ClockMemberDisplay implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int id;
	
	private int status;
	
	private int createUserId;
	
	private String createTime;
	
	private int clockId;

	private int memberId;

	private String remind;
	
	private String clockTitle;//任务标题
	
	private String clockIcon;//任务头像

	private boolean notification;
	
	private String account; //创建人账号
	
	private String picPath;//创建人头像
	
	private int userId; //关联用户的ID，对应的是account的id
	
	private int newStatus;//对方处理后的状态

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public int getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(int createUserId) {
		this.createUserId = createUserId;
	}

	public int getClockId() {
		return clockId;
	}

	public void setClockId(int clockId) {
		this.clockId = clockId;
	}

	public int getMemberId() {
		return memberId;
	}

	public void setMemberId(int memberId) {
		this.memberId = memberId;
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

	public String getClockTitle() {
		return clockTitle;
	}

	public void setClockTitle(String clockTitle) {
		this.clockTitle = clockTitle;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPicPath() {
		return picPath;
	}

	public void setPicPath(String picPath) {
		this.picPath = picPath;
	}

	public String getClockIcon() {
		return clockIcon;
	}

	public void setClockIcon(String clockIcon) {
		this.clockIcon = clockIcon;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getNewStatus() {
		return newStatus;
	}

	public void setNewStatus(int newStatus) {
		this.newStatus = newStatus;
	}
	
}
