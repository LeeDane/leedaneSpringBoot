package com.cn.leedane.model.mall;

import org.apache.solr.client.solrj.beans.Field;

import com.cn.leedane.model.RecordTimeBean;
import com.cn.leedane.mybatis.table.annotation.Column;
import com.cn.leedane.mybatis.table.annotation.Table;

/**
 * 首页分类的展示商品实体bean
 * @author LeeDane
 * 2017年12月28日 下午5:07:20
 * version 1.0
 */
@Table(value="t_mall_home_item_product")
public class S_HomeItemProductBean extends RecordTimeBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 当前分类对应的项的id
	 */
	@Column(value="item_id")
	@Field
	private long itemId;
	
	/**
	 * 当前分类对应的商品的id
	 */
	@Column(value="product_id")
	@Field
	private long productId;
	
	/**
	 * 商品的排列顺序
	 */
	@Column(value="product_order")
	@Field
	private int productOrder;

	public long getProductId() {
		return productId;
	}

	public void setProductId(long productId) {
		this.productId = productId;
	}

	public int getProductOrder() {
		return productOrder;
	}

	public void setProductOrder(int productOrder) {
		this.productOrder = productOrder;
	}

	public long getItemId() {
		return itemId;
	}

	public void setItemId(long itemId) {
		this.itemId = itemId;
	}

	
}
