package com.cn.leedane.model;



/**
 * 时间线展示的bean
 * @author LeeDane
 * 2016年7月12日 上午10:55:45
 * Version 1.0
 */
public class TimeLineBean extends IDBean{
	
	private int tableId;
	private String tableName;
	private String createTime; //避免转化json过程中的出现问题，用字符串yyyy-MM-dd HH:mm:ss字符串
	private String source;
	private String content;
	private String froms;
	private int createUserId;
	private String account;
	private String userPicPath;
	
	/**
	 * 标记类型，true才会加载source
	 */
	private boolean hasSource;
	
	public int getTableId() {
		return tableId;
	}
	public void setTableId(int tableId) {
		this.tableId = tableId;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
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
	public int getCreateUserId() {
		return createUserId;
	}
	public void setCreateUserId(int createUserId) {
		this.createUserId = createUserId;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getUserPicPath() {
		return userPicPath;
	}
	public void setUserPicPath(String userPicPath) {
		this.userPicPath = userPicPath;
	}
	public boolean isHasSource() {
		return hasSource;
	}
	public void setHasSource(boolean hasSource) {
		this.hasSource = hasSource;
	}
	
}