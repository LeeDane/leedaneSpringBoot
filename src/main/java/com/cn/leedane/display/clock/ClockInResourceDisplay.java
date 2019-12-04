package com.cn.leedane.display.clock;

import java.io.Serializable;
import java.util.List;

/**
 * 任务打卡图片信息展示类bean
 * @author LeeDane
 * 2019年1月19日 下午5:09:53
 * version 1.0
 */
public class ClockInResourceDisplay implements Serializable{
	private static final long serialVersionUID = 1L;

	private int id;

	private int status;

	private int createUserId;

	private String createTime;

	private int modifyUserId; //最终修改人

	private String modifyTime; //最终修改时间
	
	private String resource; //资源的名称
	
	private boolean main;

	private int resourceType; // 资源的类型

	private int clockId; //任务ID

	private String clockDate; // 打卡日期

	private int clockInUser; // 打卡的用户ID

	private String picPath;//创建人头像

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isMain() {
		return main;
	}

	public void setMain(boolean main) {
		this.main = main;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public int getResourceType() {
		return resourceType;
	}

	public void setResourceType(int resourceType) {
		this.resourceType = resourceType;
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

	public String getClockDate() {
		return clockDate;
	}

	public void setClockDate(String clockDate) {
		this.clockDate = clockDate;
	}

	public int getClockInUser() {
		return clockInUser;
	}

	public void setClockInUser(int clockInUser) {
		this.clockInUser = clockInUser;
	}

	public String getPicPath() {
		return picPath;
	}

	public void setPicPath(String picPath) {
		this.picPath = picPath;
	}
}
