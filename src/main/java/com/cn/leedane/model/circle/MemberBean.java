package com.cn.leedane.model.circle;

import com.cn.leedane.model.RecordTimeBean;

/**
 * 成员实体bean
 * @author LeeDane
 * 2017年5月30日 下午6:43:37
 * version 1.0
 */
public class MemberBean extends RecordTimeBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 217013073319410336L;
	
	
	//memberId和circleId组成唯一键约束
	/**
	 * 成员id,外键是UserBean的id
	 */
	private int memberId;
	
	/**
	 * 圈子id,外键是CircleBean的id
	 */
	private int circleId;
	
	/**
	 * 权限的类型，为1是创建者，2是管理者，0是普通
	 * 说明：创建者不能被删除，创建者有管理者的权限，管理者有普通者的权限
	 */
	private int roleType;

	public int getMemberId() {
		return memberId;
	}

	public void setMemberId(int memberId) {
		this.memberId = memberId;
	}

	public int getCircleId() {
		return circleId;
	}

	public void setCircleId(int circleId) {
		this.circleId = circleId;
	}

	public int getRoleType() {
		return roleType;
	}

	public void setRoleType(int roleType) {
		this.roleType = roleType;
	}
}
