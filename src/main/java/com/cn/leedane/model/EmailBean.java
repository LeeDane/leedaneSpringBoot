package com.cn.leedane.model;

import java.util.Date;
import java.util.Set;

/**
 * 邮件实体类
 * @author LeeDane
 * 2016年7月12日 上午10:43:57
 * Version 1.0
 */
//@Table(name="T_EMAIL")
public class EmailBean extends IDBean{

	private static final long serialVersionUID = 1L;
	
	/**
	 * 类型，0:新邮件，1：回复，2：转发
	 */
	private int type;
	
	/**
	 * 邮件来自(发件人)
	 */
	private UserBean from;
	
	/**
	 * 邮件标题
	 */
	private String subject;
	
	/**
	 * 收件人的列表,一对多的关系
	 */
	private Set<UserBean> replyTo; 
	
	/**
	 * 邮件内容
	 */
	private String content;
	
	/**
	 * 发送时间
	 */
	private Date createTime;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	//@OneToMany(targetEntity=UserBean.class,fetch = FetchType.EAGER,cascade=CascadeType.ALL)
	//@Column(name="user_id")
	public Set<UserBean> getReplyTo() {
		return replyTo;
	}

	public void setReplyTo(Set<UserBean> replyTo) {
		this.replyTo = replyTo;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	//@Column(name="create_time")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	//@Column(name="from_user")
	public UserBean getFrom() {
		return from;
	}

	public void setFrom(UserBean from) {
		this.from = from;
	}
	
}
