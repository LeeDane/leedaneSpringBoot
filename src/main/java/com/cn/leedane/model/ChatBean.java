package com.cn.leedane.model;


/**
 * 聊天的实体bean
 * @author LeeDane
 * 2016年7月12日 上午10:40:05
 * Version 1.0
 */
//@Table(name="T_CHAT")
public class ChatBean extends RecordTimeBean{

	private static final long serialVersionUID = 1L;
	
	private long toUserId; //接收好友的用户ID
	
	private String createUserName; //创建用户名称
	
	private String content ;  //发送消息的内容
	
	private int type; //发送消息的类型，0：文本,1:语音, 3:图片
	
	private boolean isRead; //是否读取
	
	///@Column(name="to_user_id")
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
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
	//@Column(name="is_read", columnDefinition="bit(1) default 0")
	public boolean isRead() {
		return isRead;
	}
	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}
	public String getCreateUserName() {
		return createUserName;
	}
	public void setCreateUserName(String createUserName) {
		this.createUserName = createUserName;
	}
	
	
}
