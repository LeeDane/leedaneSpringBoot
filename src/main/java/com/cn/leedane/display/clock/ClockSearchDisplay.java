package com.cn.leedane.display.clock;

import java.io.Serializable;

/**
 * 任务搜索展示类bean
 * @author LeeDane
 * 2018年10月19日 下午4:41:51
 * version 1.0
 */
public class ClockSearchDisplay implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int id;
	
	private int status;
	
	private int createUserId;
	
	private String createTime;
	
	private String title;

	private String clockDescribe;

	private String icon;

	private boolean share;
	
	private String applyEndDate;
	
	private long takePartNumber;
	
	private int rewardScore;
	
	private String startDate;
	
	private String endDate;
	
	private String clockStartTime;
	
	private String clockEndTime;
	
	private int clockInType; //任务的打卡类型(1：普通打卡， 2：图片打卡 3：位置打卡)
	
	private boolean chooseImg; //是否能从图库选择图片
			
	private boolean clockIn;//是否打卡了
		
	private String clockRepeat; //重复规则

	private String account; //创建人账号
	
	private String picPath;//创建人头像
	
	private int userId; //关联用户的ID，对应的是account的id
	
	private String shareId;//共享任务的ID，只要是共享的任务，系统自动分配共享ID
	
	private int totalDay; //总的日期
	
	private int dealStatus;//任务成员关系处理状态， -1表示是创建者，0表示没有处理关系，10表示等待成员确认， 11表示等待管理员确认
	
	private int memberNumber; //成员数量

	private boolean autoAdd; //自动加入

	private boolean autoOut; //自动退出

	private boolean seeOther; //成员互看

	private boolean clockInCheck; //打卡审核

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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getClockDescribe() {
		return clockDescribe;
	}

	public void setClockDescribe(String clockDescribe) {
		this.clockDescribe = clockDescribe;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public boolean isShare() {
		return share;
	}

	public void setShare(boolean share) {
		this.share = share;
	}

	public String getApplyEndDate() {
		return applyEndDate;
	}

	public void setApplyEndDate(String applyEndDate) {
		this.applyEndDate = applyEndDate;
	}

	public long getTakePartNumber() {
		return takePartNumber;
	}

	public void setTakePartNumber(long takePartNumber) {
		this.takePartNumber = takePartNumber;
	}

	public int getRewardScore() {
		return rewardScore;
	}

	public void setRewardScore(int rewardScore) {
		this.rewardScore = rewardScore;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getClockStartTime() {
		return clockStartTime;
	}

	public void setClockStartTime(String clockStartTime) {
		this.clockStartTime = clockStartTime;
	}

	public String getClockEndTime() {
		return clockEndTime;
	}

	public void setClockEndTime(String clockEndTime) {
		this.clockEndTime = clockEndTime;
	}

	public int getClockInType() {
		return clockInType;
	}

	public void setClockInType(int clockInType) {
		this.clockInType = clockInType;
	}

	public boolean isChooseImg() {
		return chooseImg;
	}

	public void setChooseImg(boolean chooseImg) {
		this.chooseImg = chooseImg;
	}

	public boolean isClockIn() {
		return clockIn;
	}

	public void setClockIn(boolean clockIn) {
		this.clockIn = clockIn;
	}

	public String getClockRepeat() {
		return clockRepeat;
	}

	public void setClockRepeat(String clockRepeat) {
		this.clockRepeat = clockRepeat;
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

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getShareId() {
		return shareId;
	}

	public void setShareId(String shareId) {
		this.shareId = shareId;
	}

	public int getTotalDay() {
		return totalDay;
	}

	public void setTotalDay(int totalDay) {
		this.totalDay = totalDay;
	}

	public int getDealStatus() {
		return dealStatus;
	}

	public void setDealStatus(int dealStatus) {
		this.dealStatus = dealStatus;
	}

	public int getMemberNumber() {
		return memberNumber;
	}

	public void setMemberNumber(int memberNumber) {
		this.memberNumber = memberNumber;
	}

	public boolean isAutoAdd() {
		return autoAdd;
	}

	public void setAutoAdd(boolean autoAdd) {
		this.autoAdd = autoAdd;
	}

	public boolean isAutoOut() {
		return autoOut;
	}

	public void setAutoOut(boolean autoOut) {
		this.autoOut = autoOut;
	}

	public boolean isSeeOther() {
		return seeOther;
	}

	public void setSeeOther(boolean seeOther) {
		this.seeOther = seeOther;
	}

	public boolean isClockInCheck() {
		return clockInCheck;
	}

	public void setClockInCheck(boolean clockInCheck) {
		this.clockInCheck = clockInCheck;
	}
}
