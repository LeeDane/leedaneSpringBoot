package com.cn.leedane.model;


/**
 * 赞实体类
 * @author LeeDane
 * 2016年7月12日 上午10:57:30
 * Version 1.0
 */
//@Table(name="T_ZAN")
public class ZanBean extends RecordTimeBean{
	
	private static final long serialVersionUID = 1L;
	//赞的状态,1：正常，0:禁用，2、删除
	
	/**
	 * 为点赞附加的值
	 */
	private String content;
	
	/**
	 * 设备来源
	 */
	private String froms;
	
	/**
	 * 赞对象的类型(对象表名)必须
	 */
	private String tableName;
	
	/**
	 * 赞对象的ID，必须
	 */
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
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getFroms() {
		return froms;
	}
	public void setFroms(String froms) {
		this.froms = froms;
	}
}
