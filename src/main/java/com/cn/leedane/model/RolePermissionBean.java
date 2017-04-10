package com.cn.leedane.model;

/**
 * 角色权限bean
 * @author LeeDane
 * 2017年4月10日 上午10:12:12
 * version 1.0
 */
public class RolePermissionBean extends RecordTimeBean{

	private static final long serialVersionUID = 1L;
	
	private String name; //权限的表达式

    private RoleBean role;// 一个权限对应一个角色

	public RoleBean getRole() {
		return role;
	}

	public void setRole(RoleBean role) {
		this.role = role;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
    
    
}
