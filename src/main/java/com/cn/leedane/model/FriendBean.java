package com.cn.leedane.model;


/**
 * 好友关系的实体bean
 * @author LeeDane
 * 2016年7月12日 上午10:45:05
 * Version 1.0
 */
//@Table(name="T_FRIEND")
public class FriendBean extends RecordTimeBean{

	//状态：0:请求好友，1:正式好友，2：已经删除的好友，3：黑名单好友， 4、等待我同意添加(跟0是对应的，只用于展示)， 5、对方已经拒绝
	private static final long serialVersionUID = 1L;
	
	private int fromUserId;  //发起请求好友的用户ID
	private int toUserId; //接收好友的用户ID
	
	private String fromUserRemark;  //toUserId对FromUserId对应的备注信息，必须，不能为空，默认是fromUserId的账号名
	private String toUserRemark;  //FromUserId对toUserId对应的备注信息，必须，不能为空，默认是fromUserId的账号名
	
	private String addIntroduce ;  //FromUserId对toUserId的自我介绍信息
	
	//@Column(name="from_user_id")
	public int getFromUserId() {
		return fromUserId;
	}
	public void setFromUserId(int fromUserId) {
		this.fromUserId = fromUserId;
	}
	
	//@Column(name="to_user_id")
	public int getToUserId() {
		return toUserId;
	}
	public void setToUserId(int toUserId) {
		this.toUserId = toUserId;
	}
	
	//@Column(name="from_user_remark", nullable=false)
	public String getFromUserRemark() {
		return fromUserRemark;
	}
	public void setFromUserRemark(String fromUserRemark) {
		this.fromUserRemark = fromUserRemark;
	}
	
	//@Column(name="to_user_remark", nullable=false)
	public String getToUserRemark() {
		return toUserRemark;
	}
	public void setToUserRemark(String toUserRemark) {
		this.toUserRemark = toUserRemark;
	}
	
	//@Column(name="add_introduce")
	public String getAddIntroduce() {
		return addIntroduce;
	}
	public void setAddIntroduce(String addIntroduce) {
		this.addIntroduce = addIntroduce;
	}
	
}
