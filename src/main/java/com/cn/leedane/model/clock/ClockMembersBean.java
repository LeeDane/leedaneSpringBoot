package com.cn.leedane.model.clock;

import java.util.List;

import com.cn.leedane.model.IDBean;

/**
 * 任务成员实体bean
 * @author LeeDane
 * 2018年11月30日 下午6:59:26
 * version 1.0
 */
public class ClockMembersBean extends IDBean{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 成员列表
	 */
	private List<ClockMemberBean> members;

	public List<ClockMemberBean> getMembers() {
		return members;
	}

	public void setMembers(List<ClockMemberBean> members) {
		this.members = members;
	}
	
}
