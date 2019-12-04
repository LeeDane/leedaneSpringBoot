package com.cn.leedane.model;

import org.apache.solr.client.solrj.beans.Field;

import com.cn.leedane.mybatis.table.annotation.Column;

/**
 * 权限实体bean
 * @author LeeDane
 * 2017年3月23日 下午12:54:50
 * version 1.0
 */
public class PermissionBean extends RecordTimeBean{

	private static final long serialVersionUID = 1L;

	@Column("permission_name")
	@Field
	private String name;

	@Column("permission_order")
	@Field
    private int order;//排序
    
	@Column("permission_desc")
	@Field
    private String desc; //描述信息
    
	@Column("permission_code")
	@Field
    private String code;

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
	
	
}
