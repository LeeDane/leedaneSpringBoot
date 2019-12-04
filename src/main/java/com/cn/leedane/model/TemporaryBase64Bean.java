package com.cn.leedane.model;


/**
 * 临时保存断点上传图片的表实体类
 * @author LeeDane
 * 2016年7月12日 上午10:55:13
 * Version 1.0
 */
//@Table(name="T_TEMP_BASE64")
public class TemporaryBase64Bean extends RecordTimeBean{
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * 必须，uuid,唯一(对于未保存的心情起到关联的作用)
	 */
	private String uuid;
	
	/**
	 * 最终表名归属,便于对将来多个模块的数据进行区分(必须)
	 */
	private String tableName;
	
	/**
	 * 内容
	 */
	private String content;
	
	/**
	 * 开始位置(必须)
	 */
	private long start;
	
	/**
	 * 结束位置(必须)
	 */
	private long end;
	
	/**
	 * 排序(必须)
	 */
	private int tempOrder;
	
	/**
	 * 扩展字段1
	 */
	private String str1;
	
	/**
	 * 扩展字段2
	 */
	private String str2;
	
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	//@Type(type="text")
	//@Column(name="content",nullable=false)
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getStr1() {
		return str1;
	}
	public void setStr1(String str1) {
		this.str1 = str1;
	}
	public String getStr2() {
		return str2;
	}
	public void setStr2(String str2) {
		this.str2 = str2;
	}
	
	//@Column(nullable=false)
	public long getStart() {
		return start;
	}
	public void setStart(long start) {
		this.start = start;
	}
	
	//@Column(nullable=false)
	public long getEnd() {
		return end;
	}
	public void setEnd(long end) {
		this.end = end;
	}
	
	//@Column(name="temp_order", length=2, nullable=false)
	public int getTempOrder() {
		return tempOrder;
	}
	public void setTempOrder(int tempOrder) {
		this.tempOrder = tempOrder;
	}
	
	
	//@Column(name="table_name", length=20, nullable=false)
	public String getTableName() {
		return tableName;
	}
	
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
}
