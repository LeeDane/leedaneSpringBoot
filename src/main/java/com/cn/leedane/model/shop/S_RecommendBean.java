package com.cn.leedane.model.shop;

import org.apache.solr.client.solrj.beans.Field;

import com.cn.leedane.model.RecordTimeBean;
import com.cn.leedane.mybatis.table.annotation.Column;

/**
 * 用户保存的浏览记录实体bean
 * @author LeeDane
 * 2017年11月7日 上午10:05:26
 * version 1.0
 */
public class S_RecommendBean extends RecordTimeBean{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//商品的状态：0：禁用， 1：正常
	
	/**
	 * 用户id
	 */
	@Column("user_id")
	@Field
	private int userId;
	
	/**
	 * 储存的大文本字段
	 */
	@Field
	private String content;
	
	/**
	 * 储存的大文本字段的关键字
	 */
	@Field
	@Column("key_word")
	private String keyWord;

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getKeyWord() {
		return keyWord;
	}

	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}
}
