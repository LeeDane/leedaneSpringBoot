package com.cn.leedane.model;


/**
 * 粉丝的实体bean
 * @author LeeDane
 * 2016年7月12日 上午10:43:46
 * Version 1.0
 */
//@Table(name="T_FAN")
public class FanBean extends RecordTimeBean{

	//状态：1正常
	private static final long serialVersionUID = 1L;
	private int toUserId; //接收粉丝的用户ID
	
	private String userRemark;  //FromUserId对toUserId对应的备注信息
	
	//@Column(name="to_user_id")
	public int getToUserId() {
		return toUserId;
	}
	public void setToUserId(int toUserId) {
		this.toUserId = toUserId;
	}
	//@Column(name="user_remark")
	public String getUserRemark() {
		return userRemark;
	}
	public void setUserRemark(String userRemark) {
		this.userRemark = userRemark;
	}
	
}
