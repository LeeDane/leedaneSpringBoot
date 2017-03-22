package com.cn.leedane.model;

import java.util.Date;




/**
 * 记账月报实体类
 * @author LeeDane
 * 2016年11月2日 上午11:19:32
 * Version 1.0
 */
//@Table(name="T_FINANCIAL")
public class FinancialReport extends RecordTimeBean{
	
	private static final long serialVersionUID = 1L;
	
	private int month; //记账的月份
	
	private Date startTime; //记账时间范围的开始时间
	
	private Date endTime; //记账时间范围的结束时间
	
	private float income; //收入多少钱
	
	private float spend; //支出多少钱
	
	private String desc; //其他描述信息
	
	private String detailLink; //详情链接
	
	private UserBean user;

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public float getIncome() {
		return income;
	}

	public void setIncome(float income) {
		this.income = income;
	}

	public float getSpend() {
		return spend;
	}

	public void setSpend(float spend) {
		this.spend = spend;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getDetailLink() {
		return detailLink;
	}

	public void setDetailLink(String detailLink) {
		this.detailLink = detailLink;
	}

	public UserBean getUser() {
		return user;
	}

	public void setUser(UserBean user) {
		this.user = user;
	}
	
}
