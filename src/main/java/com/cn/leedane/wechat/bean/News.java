package com.cn.leedane.wechat.bean;

/**
 * 每一条图文新的的实体
 * @author LeeDane
 * 2015年6月25日 上午11:05:15
 * Version 1.0
 */
public class News {
	private String Title;  //标题
	private String Description;  //描述
	private String PicUrl;  //图片地址
	private String Url;  //链接地址
	public String getTitle() {
		return Title;
	}
	public void setTitle(String title) {
		Title = title;
	}
	public String getDescription() {
		return Description;
	}
	public void setDescription(String description) {
		Description = description;
	}
	public String getPicUrl() {
		return PicUrl;
	}
	public void setPicUrl(String picUrl) {
		PicUrl = picUrl;
	}
	public String getUrl() {
		return Url;
	}
	public void setUrl(String url) {
		Url = url;
	}
}
