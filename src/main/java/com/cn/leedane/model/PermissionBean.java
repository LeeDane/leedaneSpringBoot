package com.cn.leedane.model;

/**
 * 权限实体bean
 * @author LeeDane
 * 2017年3月23日 下午12:54:50
 * version 1.0
 */
public class PermissionBean extends IDBean{

	private static final long serialVersionUID = 1L;

	private String name;

    private RoleBean role;// 一个权限对应一个角色

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public RoleBean getRole() {
		return role;
	}

	public void setRole(RoleBean role) {
		this.role = role;
	}
    
    
}
