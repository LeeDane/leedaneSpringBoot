package com.cn.leedane.model;

import com.cn.leedane.mybatis.table.annotation.Column;
import org.apache.solr.client.solrj.beans.Field;


/**
 * 大事件实体类
 * @author LeeDane
 * 2019年7月19日 下午19:37:24
 * Version 1.0
 */
public class EventBean extends RecordTimeBean{
	
	/**
	 * mardown语法解析成html后的数据
	 */
	@Field
	private String content;
	
	/**
	 * mardown语法源数据
	 */
	@Field
	private String source;
	
	/**
	 * 扩展字段1
	 */
	private String str1;
	
	/**
	 * 扩展字段2
	 */
	private String str2;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getStr1() {
		return str1;
	}

	public void setStr1(String str1) {
		this.str1 = str1;
	}

	public String getStr2() {
		return str2;
	}

	public void setStr2(String str2) {
		this.str2 = str2;
	}
}
