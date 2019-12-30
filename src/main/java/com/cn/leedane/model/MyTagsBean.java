package com.cn.leedane.model;


import com.cn.leedane.mybatis.table.annotation.Column;

/**
 * 我的标签实体类
 * @author LeeDane
 * 2019年12月27日 下午10:45:35
 * Version 1.0
 */
public class MyTagsBean extends RecordTimeBean{
	
	private static final long serialVersionUID = 1L;

	/**
	 * 标签名称
	 */
	@Column("tag_name")
	private String tagName;

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
}
