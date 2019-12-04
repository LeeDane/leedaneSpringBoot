package com.cn.leedane.model;

/**
 * 好友的模型
 * @author LeeDane
 * 2015年11月30日 下午1:32:46
 * Version 1.0
 */
public class FriendModel {
	private int id; //id
	private String remark; //备注
	
	public FriendModel(int id, String remark) {
		this.id = id;
		this.remark = remark;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
}
