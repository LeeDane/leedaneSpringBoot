package com.cn.leedane.model;

import com.cn.leedane.utils.EnumUtil.NotificationType;

/**
 * 通知的实体bean
 * @author LeeDane
 * 2016年7月12日 上午10:47:45
 * Version 1.0
 */
//@Table(name="T_NOTIFICATION")
public class NotificationBean extends StatusBean{

	//状态： 0：禁用， 1：正常
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 发送通知的用户
	 */
	private long fromUserId;

	/**
	 * 接收通知的用户
	 */
	private long toUserId;
	
	/**
	 * 消息的内容
	 */
	private String content;
	
	/**
	 * 消息的额外信息
	 */
	private String extra;
	
	/**
	 * 通知的类型
	 */
	private NotificationType type;
	
	/**
	 * 创建时间
	 */
	private String createTime;
	
	/**
	 * 关联的表名
	 */
	private String tableName;
	
	/**
	 * 关联的表ID
	 */
	private long tableId;
	
	/***
	 * 标记是否推送成功，默认是false
	 */
	private boolean isPushError;
	
	/**
	 * 标记该信息是否已经被阅读,默认是false
	 */
	private boolean isRead;

	//@Column(name="from_user_id")
	public long getFromUserId() {
		return fromUserId;
	}

	public void setFromUserId(long fromUserId) {
		this.fromUserId = fromUserId;
	}

	//@Column(name="to_user_id")
	public long getToUserId() {
		return toUserId;
	}

	public void setToUserId(long toUserId) {
		this.toUserId = toUserId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	} 
	
	//@Column(name="create_time")
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	//@Column(name="table_name")
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	//@Column(name="table_id")
	public long getTableId() {
		return tableId;
	}

	public void setTableId(long tableId) {
		this.tableId = tableId;
	}

	//@Column(name="is_push_error", nullable=false, columnDefinition="bit(1) default 0")
	public boolean isPushError() {
		return isPushError;
	}

	public void setPushError(boolean isPushError) {
		this.isPushError = isPushError;
	}

	//@Column(name="is_read", nullable=false, columnDefinition="bit(1) default 0")
	public boolean isRead() {
		return isRead;
	}

	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}

	public NotificationType getType() {
		return type;
	}

	public void setType(NotificationType type) {
		this.type = type;
	}
	
}
