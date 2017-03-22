package com.cn.leedane.model;

import com.cn.leedane.mybatis.table.annotation.Column;


/**
 * 关注实体类
 * @author LeeDane
 * 2016年7月12日 上午10:37:38
 * Version 1.0
 */
//@Table(name="T_ATTENTION")
public class AttentionBean extends RecordTimeBean{
	
	private static final long serialVersionUID = 1L;
	//关注的状态,1：正常，0:禁用，2、删除
	
	
	/**
	 * 关注对象的类型(对象表名)必须
	 */
	@Column("table_name")
	private String tableName;
	
	/**
	 * 关注对象的ID必须
	 */
	@Column("table_id")
	private int tableId;
	

	//@Column(name="table_name", length= 15, nullable=false)
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	//@Column(name="table_id", nullable = false)
	public int getTableId() {
		return tableId;
	}
	public void setTableId(int tableId) {
		this.tableId = tableId;
	}
}
