package com.cn.leedane.model;


/**
 * 阅读实体类
 * @author LeeDane
 * 2016年7月12日 上午10:56:05
 * Version 1.0
 */
//@Table(name="T_READ")
public class ReadBean extends RecordTimeBean{
	
	private static final long serialVersionUID = 1L;
	//阅读的状态,1：正常，0:禁用，2、删除，3、审核中， 4、审核不通过
	
	/**
	 * 来自什么方式
	 */
	private String froms;
	
	/**
	 * 阅读对象的类型(对象表名)必须
	 */
	private String tableName;
	
	/**
	 * 阅读对象的ID，必须
	 */
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
