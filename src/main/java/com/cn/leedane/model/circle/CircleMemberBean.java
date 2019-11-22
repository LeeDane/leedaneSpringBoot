package com.cn.leedane.model.circle;

import org.apache.solr.client.solrj.beans.Field;

import com.cn.leedane.model.RecordTimeBean;
import com.cn.leedane.mybatis.table.annotation.Column;
import com.cn.leedane.mybatis.table.annotation.Table;

/**
 * 成员实体bean
 * @author LeeDane
 * 2017年5月30日 下午6:43:37
 * version 1.0
 */
@Table(value="t_circle_member")
public class CircleMemberBean extends RecordTimeBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 217013073319410336L;
	
	
	//memberId和circleId组成唯一键约束
	/**
	 * 成员id,外键是UserBean的id
	 */
	@Column("member_id")
	@Field
	private long memberId;
	
	/**
	 * 圈子id,外键是CircleBean的id
	 */
	@Column("circle_id")
	@Field
	private long circleId;
	
	/**
	 * 权限的类型，为1是创建者，2是管理者，0是普通
	 * 说明：创建者不能被删除，创建者有管理者的权限，管理者有普通者的权限
	 */
	@Column("role_type")
	@Field
	private int roleType;
	
	@Column("member_score")
	@Field
	private int memberScore;
	
	@Column("member_recommend")
	@Field
	private boolean memberRecommend;

	public long getMemberId() {
		return memberId;
	}

	public void setMemberId(long memberId) {
		this.memberId = memberId;
	}

	public long getCircleId() {
		return circleId;
	}

	public void setCircleId(long circleId) {
		this.circleId = circleId;
	}

	public int getRoleType() {
		return roleType;
	}

	public void setRoleType(int roleType) {
		this.roleType = roleType;
	}

	public int getMemberScore() {
		return memberScore;
	}

	public void setMemberScore(int memberScore) {
		this.memberScore = memberScore;
	}

	public boolean getMemberRecommend() {
		return memberRecommend;
	}

	public void setMemberRecommend(boolean memberRecommend) {
		this.memberRecommend = memberRecommend;
	}
	
	
}
