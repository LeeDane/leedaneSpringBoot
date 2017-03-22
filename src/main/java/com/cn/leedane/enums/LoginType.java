package com.cn.leedane.enums;

/**
 * 平台登录类型
 * @author LeeDane
 * 2016年7月12日 上午10:23:41
 * Version 1.0
 */
public enum LoginType {
	LOGIN_TYPE_WEB("web"),  //web登录
	LOGIN_TYPE_ANDROID("android");  //android客户端登录
	

	private String value;
	private LoginType(String value){
		this.value = value;
	}
	
	public String getValue(){
		return value;
	}
}
