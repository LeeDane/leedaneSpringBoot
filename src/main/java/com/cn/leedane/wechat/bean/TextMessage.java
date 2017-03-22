package com.cn.leedane.wechat.bean;

import com.cn.leedane.wechat.bean.BaseMessage;

/**
 * 普通文本信息实体
 * @author LeeDane
 * 2015年6月24日 下午5:21:01
 * Version 1.0
 */
public class TextMessage extends BaseMessage{
	private String Content;
	private String MsgId;
	public String getContent() {
		return Content;
	}
	public void setContent(String content) {
		Content = content;
	}
	public String getMsgId() {
		return MsgId;
	}
	public void setMsgId(String msgId) {
		MsgId = msgId;
	}
}