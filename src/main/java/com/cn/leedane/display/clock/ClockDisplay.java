package com.cn.leedane.display.clock;

import java.io.Serializable;
import java.util.List;

import com.cn.leedane.mybatis.table.annotation.Column;

/**
 * 任务提醒展示类bean
 * @author LeeDane
 * 2018年8月30日 下午3:28:24
 * version 1.0
 */
public class ClockDisplay implements Serializable{

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
	
	private List<ClockUserDisplay> users;

	private int clockInType; //任务的打卡类型(1：普通打卡， 2：图片打卡 3：位置打卡)
	
	private boolean chooseImg; //是否能从图库选择图片
	
	private String clockImg; //打卡的图片
	
	private String clockLocation; //打卡位置信息
	
	private int mustStep; //打卡计步数
	
	private int leftDay;//剩余结束的天数
	
	private boolean clockIn;//是否打卡了
	
	private int totalDay; //总的日期
	
	private String clockRepeat; //重复规则
	
	private String remind; //提醒时间
	
	private int clockCount;//打卡的天数
	
	private int categoryId; //分类Id
	
	private boolean notification; //接受这个任务的通知
	
	private String shareId;//共享任务的ID，只要是共享的任务，系统自动分配共享ID
	
	private boolean autoAdd;//是否成员自动加入，默认是false，表示共享的任务，其他人员可以不通过创建者自动加入

	private boolean autoOut; //是否允许成员自动退出

	private boolean seeEachOther; //是否允许成员在动态中看大家的信息

	private boolean mustCheckClockIn; //是否成员打卡需要审核

	private int members; //成员数量
	
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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


	public List<ClockUserDisplay> getUsers() {
		return users;
	}

	public void setUsers(List<ClockUserDisplay> users) {
		this.users = users;
	}

	public int getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(int createUserId) {
		this.createUserId = createUserId;
	}

	public int getLeftDay() {
		return leftDay;
	}

	public void setLeftDay(int leftDay) {
		this.leftDay = leftDay;
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

	public boolean isClockIn() {
		return clockIn;
	}

	public void setClockIn(boolean clockIn) {
		this.clockIn = clockIn;
	}

	public int getTotalDay() {
		return totalDay;
	}

	public void setTotalDay(int totalDay) {
		this.totalDay = totalDay;
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

	public int getClockCount() {
		return clockCount;
	}

	public void setClockCount(int clockCount) {
		this.clockCount = clockCount;
	}

	public String getClockDescribe() {
		return clockDescribe;
	}

	public void setClockDescribe(String clockDescribe) {
		this.clockDescribe = clockDescribe;
	}

	public String getClockRepeat() {
		return clockRepeat;
	}

	public void setClockRepeat(String clockRepeat) {
		this.clockRepeat = clockRepeat;
	}

	public boolean isChooseImg() {
		return chooseImg;
	}

	public void setChooseImg(boolean chooseImg) {
		this.chooseImg = chooseImg;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public int getClockInType() {
		return clockInType;
	}

	public void setClockInType(int clockInType) {
		this.clockInType = clockInType;
	}

	public String getClockImg() {
		return clockImg;
	}

	public void setClockImg(String clockImg) {
		this.clockImg = clockImg;
	}

	public String getClockLocation() {
		return clockLocation;
	}

	public void setClockLocation(String clockLocation) {
		this.clockLocation = clockLocation;
	}

	public String getRemind() {
		return remind;
	}

	public void setRemind(String remind) {
		this.remind = remind;
	}

	public String getApplyEndDate() {
		return applyEndDate;
	}

	public void setApplyEndDate(String applyEndDate) {
		this.applyEndDate = applyEndDate;
	}

	public int getRewardScore() {
		return rewardScore;
	}

	public void setRewardScore(int rewardScore) {
		this.rewardScore = rewardScore;
	}

	public long getTakePartNumber() {
		return takePartNumber;
	}

	public void setTakePartNumber(long takePartNumber) {
		this.takePartNumber = takePartNumber;
	}

	public boolean isNotification() {
		return notification;
	}

	public void setNotification(boolean notification) {
		this.notification = notification;
	}

	public int getMustStep() {
		return mustStep;
	}

	public void setMustStep(int mustStep) {
		this.mustStep = mustStep;
	}

	public String getShareId() {
		return shareId;
	}

	public void setShareId(String shareId) {
		this.shareId = shareId;
	}

	public boolean isAutoAdd() {
		return autoAdd;
	}

	public void setAutoAdd(boolean autoAdd) {
		this.autoAdd = autoAdd;
	}

	public int getMembers() {
		return members;
	}

	public void setMembers(int members) {
		this.members = members;
	}

	public boolean isAutoOut() {
		return autoOut;
	}

	public void setAutoOut(boolean autoOut) {
		this.autoOut = autoOut;
	}

	public boolean isSeeEachOther() {
		return seeEachOther;
	}

	public void setSeeEachOther(boolean seeEachOther) {
		this.seeEachOther = seeEachOther;
	}

	public boolean isMustCheckClockIn() {
		return mustCheckClockIn;
	}

	public void setMustCheckClockIn(boolean mustCheckClockIn) {
		this.mustCheckClockIn = mustCheckClockIn;
	}
}
