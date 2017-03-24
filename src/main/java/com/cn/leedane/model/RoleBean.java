package com.cn.leedane.model;

import java.util.List;

/**
 * 用户角色的实体类(需要单独去维持)
 * @author LeeDane
 * 2016年7月12日 上午10:57:07
 * Version 1.0
 */
//@Table(name="T_USER_ROLE")
public class RoleBean extends IDBean{
	
	private static final long serialVersionUID = 1L;

    private String name;
    
    private List<PermissionBean> permissions;// 一个角色对应多个权限
    
    private List<UserBean> users;// 一个角色对应多个用户

	public List<PermissionBean> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<PermissionBean> permissions) {
		this.permissions = permissions;
	}

	public List<UserBean> getUsers() {
		return users;
	}

	public void setUsers(List<UserBean> users) {
		this.users = users;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
}
