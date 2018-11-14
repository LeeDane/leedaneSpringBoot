package com.cn.leedane.model.clock;

import java.util.Date;
import java.util.List;

import com.cn.leedane.model.RecordTimeBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.mybatis.table.annotation.Column;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.EnumUtil.ClockInType;

/**
 * 任务提醒实体bean
 * @author LeeDane
 * 2018年8月29日 上午10:40:03
 * version 1.0
 */
public class ClockBean extends RecordTimeBean{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//任务的状态：0：禁用， 1：正常，2：删除，9：结束
	
	/**
	 * 任务的标题，必须字段
	 */
	@Column(value="title", required = true)
	private String title;
	
	/**
	 * 任务的描述，必须字段(为空将以标题代替)
	 */
	@Column(value="describe_", required = true)
	private String describe_;
	
	/**
	 * 任务的图标，必须字段(为空将以显示默认的无图照片)
	 */
	@Column(value="icon", required = true)
	private String icon;
	
	/**
	 * 任务的类型(false:私有默认是false， true：共享，共享的任务将支持外部的搜索：title, describe等字段)
	 */
	@Column(value="share", required = true)
	private boolean share;
	
	/**
	 * 当是共享模式的情况下，最迟的报名日期，可以为空，为空表示不限制报名日期
	 */
	@Column(value="apply_end_date", required = true)
	private Date applyEndDate;
	
	/**
	 * 当是共享模式的情况下，最大参与人数，必须大于1小于当前用户等级的最大数
	 */
	@Column(value="take_part_number", required = true)
	private long takePartNumber;
	
	/**
	 * 当是共享模式的情况下，每个参与的用户在结束之后，成功参与的用户获得报酬的积分, 必须大于0
	 * 个人任务：发起用户的积分必须大于等于takePartNumber * rewardScore，发布成功系统将提前代扣，等任务结束后结算再返还
	 * 系统任务：不做限制
	 */
	/**
	 * 当是共享模式的情况下，参与的用户在结束之后，不成功参与的用户将被扣除的积分, 必须大于0
	 * 个人任务：成功参与进来的用户，每个人将扣除 punishmentScore积分，同时也扣创建者积分，等任务结束后结算再决定是否返还
	 * 系统任务：成功参与进来的用户，每个人将扣除 punishmentScore积分，等任务结束后结算再决定是否返还
	 */
	@Column(value="reward_score", required = true)
	private int rewardScore;
	
	/**
	 * 当是共享模式的情况下，参与的用户在结束之后，不成功参与的用户将被扣除的积分, 必须大于0
	 * 个人任务：成功参与进来的用户，每个人将扣除 punishmentScore积分，等任务结束后结算再决定是否返还
	 * 系统任务：成功参与进来的用户，每个人将扣除 punishmentScore积分，等任务结束后结算再决定是否返还
	 */
//	@Column(value="punishment_score", required = true)
//	private int punishmentScore;
	
	/**
	 * 任务开始时间（不能为空）
	 */
	@Column(value="start_date", required = true)
	private Date startDate;
	
	/**
	 * 任务结束时间（可以为空, 为空表示无穷）
	 */
	@Column(value="end_date", required = true)
	private Date endDate;
	
	/**
	 * 任务打卡开始时间（可以为空, 为空表示不限定打卡开始时间）
	 */
	@Column(value="clock_start_time", required = true)
	private Date clockStartTime;
	
	/**
	 * 任务打卡结束时间（可以为空, 为空表示不限定打卡时间）
	 */
	@Column(value="clock_end_time", required = true)
	private Date clockEndTime;
	
	/**
	 * 这个任务关联的用户列表
	 */
	@Column(required = false)
	private List<UserBean> users;
	
	/**
	 * 任务的打卡类型(1：普通打卡， 2：图片打卡 3：位置打卡, 4:计步打卡)
	 */
	@Column(value="clock_in_type", required = true)
	private int clockInType;
	
	/**
	 * 任务的打卡是否可以选择图片(false:不可以默认是false， true：可以)
	 */
	@Column(value="choose_img", required = true)
	private boolean chooseImg;
	
	
	/**
	 * 任务的重复规则(必须的)
	 */
	@Column(value="repeat_", required = true)
	private String repeat_; //重复规则
	
	/**
	 * 任务的计步打卡，当clock_in_type为计步打卡的时候，用户计步打卡最低的要求
	 */
	@Column(value="must_step", required = true)
	private int mustStep;
	
	/**
	 * 分类的ID，目前主要用于系统的任务
	 */
	@Column(value="category_id", required = true)
	private int categoryId;
	
	/**
	 * 父任务的ID，一般只作用于用户行为的任务，不作用系统任务
	 */
	@Column(value="parent_id", required = true)
	private int parentId;
	
	/**
	 * 共享任务的ID，只要是共享的任务，系统自动分配共享ID
	 */
	@Column(value="share_id", required = true)
	private String shareId;
	
	/**
	 * 需要打卡的总天数,如果无穷，默认是-1
	 */
	@Column(value="total_day", required = true)
	private int totalDay;
	
	/**
	 * 是否成员自动加入，默认是true，表示共享的任务，其他人员可以不通过创建者自动加入
	 */
	@Column(value="auto_add", required = true)
	private boolean autoAdd;

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

	public List<UserBean> getUsers() {
		return users;
	}

	public void setUsers(List<UserBean> users) {
		this.users = users;
	}

	public Date getClockStartTime() {
		return clockStartTime;
	}

	public void setClockStartTime(Date clockStartTime) {
		this.clockStartTime = clockStartTime;
	}

	public Date getClockEndTime() {
		return clockEndTime;
	}

	public void setClockEndTime(Date clockEndTime) {
		this.clockEndTime = clockEndTime;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getDescribe_() {
		return describe_;
	}

	public void setDescribe_(String describe_) {
		this.describe_ = describe_;
	}

	public String getRepeat_() {
		return repeat_;
	}

	public void setRepeat_(String repeat_) {
		this.repeat_ = repeat_;
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

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}
	public int getClockInType() {
		return clockInType;
	}

	public void setClockInType(int clockInType) {
		if(clockInType < 1 || clockInType > 4)
			clockInType = EnumUtil.ClockInType.普通打卡.value;
		this.clockInType = clockInType;
	}

	public Date getApplyEndDate() {
		return applyEndDate;
	}

	public void setApplyEndDate(Date applyEndDate) {
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

	public int getTotalDay() {
		return totalDay;
	}

	public void setTotalDay(int totalDay) {
		this.totalDay = totalDay;
	}

	public boolean isAutoAdd() {
		return autoAdd;
	}

	public void setAutoAdd(boolean autoAdd) {
		this.autoAdd = autoAdd;
	}
	
	

//	public int getPunishmentScore() {
//		return punishmentScore;
//	}
//
//	public void setPunishmentScore(int punishmentScore) {
//		this.punishmentScore = punishmentScore;
//	}
	
}
