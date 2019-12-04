package com.cn.leedane.netty;

import java.io.Serializable;

public class PushMsg implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String author_name;
    public String date;
    public String thumbnail_pic_s;//图片链接
    public String title;
    public String url;//详情链接
	public String getAuthor_name() {
		return author_name;
	}
	public void setAuthor_name(String author_name) {
		this.author_name = author_name;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getThumbnail_pic_s() {
		return thumbnail_pic_s;
	}
	public void setThumbnail_pic_s(String thumbnail_pic_s) {
		this.thumbnail_pic_s = thumbnail_pic_s;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
    
    
}
