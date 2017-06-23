package com.cn.leedane.model;

import org.apache.solr.client.solrj.beans.Field;

import com.cn.leedane.mybatis.table.annotation.Column;


/**
 * 访客实体类
 * @author LeeDane
 * 2017年5月11日 下午4:33:52
 * version 1.0
 */
//@Table(name="T_VISITOR")
public class VisitorBean extends RecordTimeBean{
	
	private static final long serialVersionUID = 1L;
	//访客的状态,1：正常，0:禁用，2、删除
	
	/**
	 * 来自什么方式
	 */
	private String froms;
	
	/**
	 * 访客对象的类型(对象表名)必须
	 */
	@Column("table_name")
	@Field
	private String tableName;
	
	/**
	 * 访客对象的表ID，必须
	 */
	@Column("table_id")
	@Field
	private int tableId;

	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	public int getTableId() {
		return tableId;
	}
	public void setTableId(int tableId) {
		this.tableId = tableId;
	}
	public String getFroms() {
		return froms;
	}
	public void setFroms(String froms) {
		this.froms = froms;
	}
}
