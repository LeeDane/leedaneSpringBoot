package com.cn.leedane.display.clock;

import java.io.Serializable;

/**
 * 任务动态展示类bean
 * @author LeeDane
 * 2018年11月29日 下午5:14:53
 * version 1.0
 */
public class ClockDynamicDisplay implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long id;
	
	private int status;
	
	private long createUserId;
	
	private String createTime;
	
	private String modifyTime; //最终修改时间
	
	private long clockId;

	private String desc;
	
	private String account; //创建人账号
	
	private String picPath;//创建人头像

	private int messageType;

	private boolean seeEachOther;

	public long getId() {
		return id;
	}

	public void setId(long id) {
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

	public long getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(long createUserId) {
		this.createUserId = createUserId;
	}

	public long getClockId() {
		return clockId;
	}

	public void setClockId(long clockId) {
		this.clockId = clockId;
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


	public String getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(String modifyTime) {
		this.modifyTime = modifyTime;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public int getMessageType() {
		return messageType;
	}

	public void setMessageType(int messageType) {
		this.messageType = messageType;
	}

	public boolean isSeeEachOther() {
		return seeEachOther;
	}

	public void setSeeEachOther(boolean seeEachOther) {
		this.seeEachOther = seeEachOther;
	}
}
