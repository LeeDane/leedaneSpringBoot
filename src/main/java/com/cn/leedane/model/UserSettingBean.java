package com.cn.leedane.model;

import org.apache.solr.client.solrj.beans.Field;

import com.cn.leedane.model.RecordTimeBean;
import com.cn.leedane.mybatis.table.annotation.Column;
import com.cn.leedane.mybatis.table.annotation.Table;

/**
 * 个人中心设置
 * @author LeeDane
 * 2017年8月15日 下午1:20:43
 * version 1.0
 */
@Table(value="t_user_setting")
public class UserSettingBean extends RecordTimeBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3562775071804007807L;
	
	/**
	 * 是否可以添加好友
	 */
	@Column("add_friend")
	@Field
	private boolean addFriend;
	
	/**
	 * 欢迎成员的信息(支持el表达式)，为空将不发送
	 */
	@Column("welcome_friend")
	@Field
	private String welcomeFriend;
	
	/**
	 * 是否需要审核圈子的帖子
	 */
	@Column("check_post")
	@Field
	private boolean checkPost;
	
	/**
	 * 背景颜色
	 */
	@Column("background_color")
	@Field
	private String backgroundColor;
	
	/**
	 * 限制成员的总数
	 */
	@Column("limit_number")
	@Field
	private int limitNumber;
	
	
	public boolean isCheckPost() {
		return checkPost;
	}

	public void setCheckPost(boolean checkPost) {
		this.checkPost = checkPost;
	}

	public String getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public int getLimitNumber() {
		return limitNumber;
	}

	public void setLimitNumber(int limitNumber) {
		this.limitNumber = limitNumber;
	}
	
	
}
