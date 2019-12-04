package com.cn.leedane.display.clock;

import java.io.Serializable;
import java.util.List;

/**
 * 任务打卡信息展示类bean
 * @author LeeDane
 * 2019年1月19日 下午5:09:53
 * version 1.0
 */
public class ClockInDisplay implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int id;
	
	private int status;
	
	private int createUserId;
	
	private String createTime;

	private int modifyUserId; //最终修改人
	
	private String modifyTime; //最终修改时间

	private String account; //创建人账号

	private String picPath;//创建人头像

	private int clockId; //任务ID

	private int clockCreateUserId;//任务创建人ID

	private String icon; //任务图标

	private String title;//任务标题

	/**
	 * 任务的方式
	 */
	private String froms;

	/**
	 * 任务的打卡图片，当任务type为2的时候是必须字段
	 */
	private List<ClockInResourceDisplay> resources;

	/**
	 * 任务的打卡位置，当任务type为3的时候是必须字段
	 */
	private String location;

	/**
	 * 任务的计步打卡，当任务type为4的时候是必须字段
	 */
	private int step;

	/**
	 * 任务的日期
	 */
	private String clockDate;

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

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

	public int getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(int createUserId) {
		this.createUserId = createUserId;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public int getModifyUserId() {
		return modifyUserId;
	}

	public void setModifyUserId(int modifyUserId) {
		this.modifyUserId = modifyUserId;
	}

	public String getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(String modifyTime) {
		this.modifyTime = modifyTime;
	}

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

	public String getClockDate() {
		return clockDate;
	}

	public void setClockDate(String clockDate) {
		this.clockDate = clockDate;
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

	public List<ClockInResourceDisplay> getResources() {
		return resources;
	}

	public void setResources(List<ClockInResourceDisplay> resources) {
		this.resources = resources;
	}

	public int getClockCreateUserId() {
		return clockCreateUserId;
	}

	public void setClockCreateUserId(int clockCreateUserId) {
		this.clockCreateUserId = clockCreateUserId;
	}
}
