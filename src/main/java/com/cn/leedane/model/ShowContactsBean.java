package com.cn.leedane.model;

import java.util.List;


/**
 * 服务器返回匹配后的联系人列表展示信息
 * @author LeeDane
 * 2016年7月12日 上午10:54:21
 * Version 1.0
 */
public class ShowContactsBean {
	private int id;
	private List<PhoneNumber> phoneNumbers;
	private String name;
	private boolean match; //对于多个电话号码，只要至少一个匹配
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public List<PhoneNumber> getPhoneNumbers() {
		return phoneNumbers;
	}
	public void setPhoneNumbers(List<PhoneNumber> phoneNumbers) {
		this.phoneNumbers = phoneNumbers;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isMatch() {
		return match;
	}
	public void setMatch(boolean match) {
		this.match = match;
	}
	
	
}

class PhoneNumber{
	private String phoneNumber;
	private boolean match; //是否匹配
	private String userName; //匹配成功后的用户名称
	private String userId; //匹配成功后用户ID
	private String userPicPath; //匹配成功后的用户头像地址
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public boolean isMatch() {
		return match;
	}
	public void setMatch(boolean match) {
		this.match = match;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserPicPath() {
		return userPicPath;
	}
	public void setUserPicPath(String userPicPath) {
		this.userPicPath = userPicPath;
	}
}
