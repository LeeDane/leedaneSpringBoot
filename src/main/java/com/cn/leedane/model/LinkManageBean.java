package com.cn.leedane.model;

import org.apache.solr.client.solrj.beans.Field;

import com.cn.leedane.mybatis.table.annotation.Column;


/**
 * 链接权限管理实体bean
 * @author LeeDane
 * 2017年4月10日 下午4:41:15
 * version 1.0
 */
//@Table(name="T_LINK_MANAGE")
public class LinkManageBean extends RecordTimeBean{
	
	private static final long serialVersionUID = 1L;
    
	@Column("link")
	@Field
    private String link;// 一个角色对应多个权限
    
	@Column("alias")
	@Field
    private String alias; // 别名，不能为空且唯一
    
	@Column(required = false)
    private String roleOrPermissionCodes;//权限Code集合，多个用,分开
    
    @Column("role")
	@Field
    private boolean role; //类型：true表示roleCode不能为空，false表示permissionCode不能为空
    
    @Column("order_")
	@Field
    private int order_; //排序
    
    @Column("all_")
	@Field
    private boolean all_; //是否是全部都符合，true是全部都符合，false是任意一个符合，默认是true

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	public String getRoleOrPermissionCodes() {
		return roleOrPermissionCodes;
	}

	public void setRoleOrPermissionCodes(String roleOrPermissionCodes) {
		this.roleOrPermissionCodes = roleOrPermissionCodes;
	}

	public boolean isRole() {
		return role;
	}

	public void setRole(boolean role) {
		this.role = role;
	}

	public int getOrder_() {
		return order_;
	}

	public void setOrder_(int order_) {
		this.order_ = order_;
	}

	public boolean isAll_() {
		return all_;
	}

	public void setAll_(boolean all_) {
		this.all_ = all_;
	}
}
