package com.cn.leedane.model;

/**
 * 链接角色或者权限bean
 * @author LeeDane
 * 2017年4月20日 下午5:38:41
 * Version 1.0
 */
public class LinkRoleOrPermissionBean extends RecordTimeBean{

	private static final long serialVersionUID = 1L;
	
    private int roleId; //对应角色id
    
    private int permissionId; //对应权限id
    
    private boolean role;
    
    private int linkId; //链接Id

	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	public int getPermissionId() {
		return permissionId;
	}

	public void setPermissionId(int permissionId) {
		this.permissionId = permissionId;
	}

	public boolean isRole() {
		return role;
	}

	public void setRole(boolean role) {
		this.role = role;
	}

	public int getLinkId() {
		return linkId;
	}

	public void setLinkId(int linkId) {
		this.linkId = linkId;
	}
}
