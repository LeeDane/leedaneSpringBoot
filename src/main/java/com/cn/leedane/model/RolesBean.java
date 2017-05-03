package com.cn.leedane.model;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author LeeDane
 * 2017年5月3日 下午2:38:19
 * version 1.0
 */
public class RolesBean implements Serializable{
	
private static final long serialVersionUID = 1L;
    
	private List<RoleBean> roleBeans;

	public List<RoleBean> getRoleBeans() {
		return roleBeans;
	}

	public void setRoleBeans(List<RoleBean> roleBeans) {
		this.roleBeans = roleBeans;
	}
	
}
