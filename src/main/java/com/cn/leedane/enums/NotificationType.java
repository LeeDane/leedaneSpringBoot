package com.cn.leedane.enums;

/**
 * 通知类型
 * @author LeeDane
 * 2016年7月12日 上午10:23:48
 * Version 1.0
 */
public enum NotificationType {
	ADD_FRIEND("addFriend"),  //增加好友
	AGREE_FRIEND("agreeFriend"),  //同意好友
	PUBLISHED_MOOD("publishedMood"),  //发表说说
	ADD_COMMENT("addComment"),  //添加评论
	REGISTER_VALIDATION("register"), //用户注册验证码
	LOGIN_VALIDATION("login"), //用户登录验证码
	UPDATE_PSW_VALIDATION("updatePSW"), //修改密码验证码
	UPDATE_INFO_VALIDATION("updateInfo"), //信息变更验证码
	LOGIN_ERROR_VALIDATION("loginError"), //登录异常验证码
	IDENTITY_VALIDATION("identity"), //身份验证验证码
	ACTIVITY_VALIDATION("activity"); //活动确认验证码

	public String value;
	private NotificationType(String value){
		this.value = value;
	}
}
