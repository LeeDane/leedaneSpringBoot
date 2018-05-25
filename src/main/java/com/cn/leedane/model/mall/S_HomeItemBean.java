package com.cn.leedane.model.mall;

import org.apache.solr.client.solrj.beans.Field;

import com.cn.leedane.model.RecordTimeBean;
import com.cn.leedane.mybatis.table.annotation.Column;
import com.cn.leedane.mybatis.table.annotation.Table;

/**
 * 商品首页分类实体
 * @author LeeDane
 * 2017年12月27日 下午7:28:58
 * version 1.0
 */
@Table(value="t_mall_home_item")
public class S_HomeItemBean extends RecordTimeBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * 分类的ID
	 */
	@Column("category_id")
	@Field
	private int categoryId;
	
	/**
	 * 展示用的分类字段
	 */
	@Column(required=false)
	private String categoryText;
	
	/**
	 * 排列顺序，大的先出，小的后出
	 */
	@Column("category_order")
	@Field
	private int categoryOrder;
	
	/**
	 * 展示的子分类json字符串(一开始默认是整个儿子节点的列表)
	 */
	@Column("children")
	@Field
	private String children;

	/**
	 * 展示的数量
	 */
	@Field
	private int number;

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public int getCategoryOrder() {
		return categoryOrder;
	}

	public void setCategoryOrder(int categoryOrder) {
		this.categoryOrder = categoryOrder;
	}

	public String getChildren() {
		return children;
	}

	public void setChildren(String children) {
		this.children = children;
	}

	public String getCategoryText() {
		return categoryText;
	}

	public void setCategoryText(String categoryText) {
		this.categoryText = categoryText;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}
}
