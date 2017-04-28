package com.cn.leedane.shiro;

import org.apache.shiro.authc.UsernamePasswordToken;

import com.cn.leedane.model.UserBean;
import com.cn.leedane.utils.EnumUtil.PlatformType;

/**
 * 自定义身份校验
 * @author LeeDane
 * 2017年3月24日 上午10:59:54
 * version 1.0
 */
public class CustomAuthenticationToken extends UsernamePasswordToken{
	
	//标记平台的类型
	private PlatformType platformType = PlatformType.网页版;
	
	//非网页平台要校验的token码
	private String token;
	
	//用户的唯一ID
	private int userId;
	
	private UserBean user;
	
	private String FromUserName; //微信绑定的时候专用的变量

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PlatformType getPlatformType() {
		return platformType;
	}

	public void setPlatformType(PlatformType platformType) {
		this.platformType = platformType;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public UserBean getUser() {
		return user;
	}

	public void setUser(UserBean user) {
		this.user = user;
	}

	public String getFromUserName() {
		return FromUserName;
	}

	public void setFromUserName(String fromUserName) {
		FromUserName = fromUserName;
	}
}
