package com.cn.leedane.model;

import org.apache.solr.client.solrj.beans.Field;

import com.cn.leedane.mybatis.table.annotation.Column;

/**
 * 聊天广场实体Bean
 * @author LeeDane
 * 2017年2月10日 下午4:05:01
 * Version 1.0
 */
public class ChatSquareBean extends RecordTimeBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String content; //聊天内容
	
	private String type; //消息内容
	
	@Column("at_other")
	@Field
	private String atOther; //at某人，多个用,分开

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAtOther() {
		return atOther;
	}

	public void setAtOther(String atOther) {
		this.atOther = atOther;
	}
	
	

}
