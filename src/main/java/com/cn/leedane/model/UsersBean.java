package com.cn.leedane.model;

import java.io.Serializable;


/**
 * 测试
 * @author LeeDane
 * 2018年12月5日 下午1:11:33
 * version 1.0
 */
public class UsersBean implements Serializable{
	
	
	private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}
