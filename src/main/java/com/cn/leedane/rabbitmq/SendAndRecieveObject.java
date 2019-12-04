package com.cn.leedane.rabbitmq;

import java.io.Serializable;
import java.util.Date;
/**
 * 接收和发送的对象
 * @author LeeDane
 * 2015年9月6日 下午6:19:33
 * Version 1.0
 */
public class SendAndRecieveObject implements Serializable{

	private static final long serialVersionUID = 1L;

	/**
	 * 默认为队列名称增加的后缀名
	 */
	public static final String DEFAULT_SUFFIX = "RabbitMQ";
	
	private String toUserID; //目标的用户ID
	private String fromUserID;  //发送的用户ID
	private Date createTime;  //发送时间
	private String msg;  //信息数组
	public String getToUserID() {
		return toUserID;
	}
	public void setToUserID(String toUserID) {
		this.toUserID = toUserID;
	}
	public String getFromUserID() {
		return fromUserID;
	}
	public void setFromUserID(String fromUserID) {
		this.fromUserID = fromUserID;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
}
