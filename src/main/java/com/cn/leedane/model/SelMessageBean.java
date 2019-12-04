package com.cn.leedane.model;

import java.util.Set;

/**
 * 私信实体类
 * @author LeeDane
 * 2016年7月12日 上午10:53:43
 * Version 1.0
 */
//@Table(name="T_SEL_MESSAGE")
public class SelMessageBean extends RecordTimeBean{
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * 发送私信的用户(谁发送的)，不能为空
	 */
	private UserBean fromUser; 
	
	/**
	 * 接收私信的用户(发给谁)，不能为空
	 * 一对多的关系
	 */
	private Set<UserBean> toUser; 
	
	/**
	 * 是否发送
	 */
	private boolean isSend;
	
	/**
	 * 是否接收
	 */
	private boolean isRecieve;
	
	/**
	 * 发送的内容(不能为空)
	 */
	private String content;

	//@ManyToOne(targetEntity = UserBean.class)
	//@JoinColumn(name="from_user_id", referencedColumnName="id", nullable=false)//外键为from_user_id，与user中的id关联
	public UserBean getFromUser() {
		return fromUser;
	}

	public void setFromUser(UserBean fromUser) {
		this.fromUser = fromUser;
	}

	//@ManyToOne(targetEntity = UserBean.class,fetch = FetchType.EAGER,cascade=CascadeType.ALL)
	//@JoinColumn(name="to_user_id", referencedColumnName="id", nullable=false)//外键为to_user_id，与user中的id关联

	public Set<UserBean> getToUser() {
		return toUser;
	}

	public void setToUser(Set<UserBean> toUser) {
		this.toUser = toUser;
	}


	//@Column(name="is_send")
	public boolean isSend() {
		return isSend;
	}

	public void setSend(boolean isSend) {
		this.isSend = isSend;
	}

	//@Column(name="is_recieve")
	public boolean isRecieve() {
		return isRecieve;
	}

	public void setRecieve(boolean isRecieve) {
		this.isRecieve = isRecieve;
	}

	//@Type(type="text")
	//@Column(name="content",nullable=false)  //不能为空
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
		
}
