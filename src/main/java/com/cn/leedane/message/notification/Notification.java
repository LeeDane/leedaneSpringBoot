package com.cn.leedane.message.notification;

import com.cn.leedane.model.UserBean;

/**
 * 通知的消息
 * @author LeeDane
 * 2016年7月12日 下午2:22:12
 * Version 1.0
 */
public class Notification {
	
	/**
	 * 发送通知的用户
	 */
	private UserBean fromUser; 

	/**
	 * 需要通知的用户
	 */
	private UserBean toUser; 
	
	/**
	 * 消息的内容
	 */
	private String content;
	
	/**
	 * 通知的类型
	 */
	private String type;

	public UserBean getFromUser() {
		return fromUser;
	}

	public void setFromUser(UserBean fromUser) {
		this.fromUser = fromUser;
	}

	public UserBean getToUser() {
		return toUser;
	}

	public void setToUser(UserBean toUser) {
		this.toUser = toUser;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	} 
	
	
}
