package com.cn.leedane.model;


/**
 * 用户角色的关系实体类
 * @author LeeDane
 * 2016年7月12日 上午10:57:07
 * Version 1.0
 */
//@Table(name="T_USER_ROLE")
public class UserRoleBean extends RecordTimeBean{
	
	private static final long serialVersionUID = 1L;
    
    private RoleBean role;// 一个角色对应多个权限
    
    private UserBean user;// 一个角色对应多个用户

	public RoleBean getRole() {
		return role;
	}

	public void setRole(RoleBean role) {
		this.role = role;
	}

	public UserBean getUser() {
		return user;
	}

	public void setUser(UserBean user) {
		this.user = user;
	}
}
