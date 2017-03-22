package com.cn.leedane.model;

import java.io.Serializable;

public class Rows implements Serializable{

	/**
	 * 
	 * create time 2015年7月4日 下午12:09:40
	 */
	private static final long serialVersionUID = 1L;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	private String name;
	private int age;
	private String phone;
	
	
}
