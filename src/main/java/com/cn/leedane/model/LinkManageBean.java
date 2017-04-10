package com.cn.leedane.model;


/**
 * 链接权限管理实体bean
 * @author LeeDane
 * 2017年4月10日 下午4:41:15
 * version 1.0
 */
//@Table(name="T_LINK_MANAGE")
public class LinkManageBean extends RecordTimeBean{
	
	private static final long serialVersionUID = 1L;
    
    private String link;// 一个角色对应多个权限
    
    private String alias; // 别名，不能为空且唯一
    
    private String permissionCodes;//权限Code集合，多个用,分开
    
    private String roleCodes; //角色Code集合，多个用,分开
    
    private int type; //类型：1表示permissionId不能为空，2表示roleId不能为空
    
    private int order; //排序

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

	
	public String getPermissionCodes() {
		return permissionCodes;
	}

	public void setPermissionCodes(String permissionCodes) {
		this.permissionCodes = permissionCodes;
	}

	public String getRoleCodes() {
		return roleCodes;
	}

	public void setRoleCodes(String roleCodes) {
		this.roleCodes = roleCodes;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}
}
