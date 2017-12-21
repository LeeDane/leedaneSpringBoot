package com.cn.leedane.netty;

import java.io.Serializable;

/**
 * 聊天消息的实体
 * @author LeeDane
 * 2017年11月1日 上午11:36:10
 * version 1.0
 */
public class ChatMessage implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private byte type;
    private PushMsg msg;
	public byte getType() {
		return type;
	}
	public void setType(byte type) {
		this.type = type;
	}
	public PushMsg getMsg() {
		return msg;
	}
	public void setMsg(PushMsg msg) {
		this.msg = msg;
	}
    
    
}
