package com.cn.leedane.wechat.bean;

import com.cn.leedane.wechat.bean.BaseMessage;

/**
 * 图文消息实体
 * @author LeeDane
 * 2015年6月24日 下午5:20:49
 * Version 1.0
 */
public class ImageMessage extends BaseMessage{
	private String PicUrl; //图片链接 
	private String MediaId; //图片消息媒体id，可以调用多媒体文件下载接口拉取数据
	private String MsgId; //消息id，64位整型 
	public String getPicUrl() {
		return PicUrl;
	}
	public void setPicUrl(String picUrl) {
		PicUrl = picUrl;
	}
	public String getMediaId() {
		return MediaId;
	}
	public void setMediaId(String mediaId) {
		MediaId = mediaId;
	}
	public String getMsgId() {
		return MsgId;
	}
	public void setMsgId(String msgId) {
		MsgId = msgId;
	}
}