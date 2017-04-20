package com.cn.leedane.model;

import java.util.List;

import org.apache.solr.client.solrj.beans.Field;

import com.cn.leedane.mybatis.table.annotation.Column;


/**
 * 用户角色的实体类(需要单独去维持)
 * @author LeeDane
 * 2016年7月12日 上午10:57:07
 * Version 1.0
 */
//@Table(name="T_USER_ROLE")
public class RoleBean extends RecordTimeBean{
	
	private static final long serialVersionUID = 1L;

	@Column("role_name")
	@Field
    private String name;
        
	@Column("role_order")
	@Field
    private int order; //角色排序
    
	@Column("role_desc")
	@Field
    private String desc; //角色描述信息
    
	@Column("role_code")
	@Field
    private String code; //唯一编码
    
    private List<PermissionBean> permissions; //权限的列表

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public List<PermissionBean> getPermissions() {
		return permissions;
	}

	public void setPermissions(List<PermissionBean> permissions) {
		this.permissions = permissions;
	}
	
}
