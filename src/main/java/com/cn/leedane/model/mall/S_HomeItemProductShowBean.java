package com.cn.leedane.model.mall;

import com.cn.leedane.model.RecordTimeBean;

/**
 * 首页分类的展示商品前端展示用的实体bean
 * @author LeeDane
 * 2018年1月5日 上午11:37:35
 * version 1.0
 */
public class S_HomeItemProductShowBean extends RecordTimeBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 当前分类对应的项的id
	 */
	private long itemId;
	
	private int order_;
	
	private S_ProductBean productBean;

	public long getItemId() {
		return itemId;
	}

	public void setItemId(long itemId) {
		this.itemId = itemId;
	}

	public int getOrder_() {
		return order_;
	}

	public void setOrder_(int order_) {
		this.order_ = order_;
	}

	public S_ProductBean getProductBean() {
		return productBean;
	}

	public void setProductBean(S_ProductBean productBean) {
		this.productBean = productBean;
	}
}
