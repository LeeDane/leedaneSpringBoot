package com.cn.leedane.model.mall;

import java.util.List;

import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.KeyValueBean;

/**
 * 首页分页的展示对象实体bean 
 * @author LeeDane
 * 2017年12月28日 下午4:32:31
 * version 1.0
 */
public class S_HomeItemShowBean extends IDBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 首页分页存储的id, 用来判断是否是可以编辑的
	 */
	private long itemId;
	
	/**
	 * 当前分类的id
	 */
	private long categoryId;
	
	/**
	 * 当前分类的名称
	 */
	private String categoryText;
	
	/**
	 * 展示的的子分类列表
	 */
	private List<KeyValueBean> childrens;
	
	/**
	 * 展示的商品列表
	 */
	private List<S_HomeItemProductShowBean> productBeans;
	
	/**
	 * 排序号
	 */
	private int order;
	
	/**
	 * 数量
	 */
	private int number;
	

	public long getItemId() {
		return itemId;
	}

	public void setItemId(long itemId) {
		this.itemId = itemId;
	}

	public String getCategoryText() {
		return categoryText;
	}

	public void setCategoryText(String categoryText) {
		this.categoryText = categoryText;
	}

	public List<KeyValueBean> getChildrens() {
		return childrens;
	}

	public void setChildrens(List<KeyValueBean> childrens) {
		this.childrens = childrens;
	}

	public List<S_HomeItemProductShowBean> getProductBeans() {
		return productBeans;
	}

	public void setProductBeans(List<S_HomeItemProductShowBean> productBeans) {
		this.productBeans = productBeans;
	}

	public long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}
}
