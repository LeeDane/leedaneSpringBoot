package com.cn.leedane.model.mall;

import org.apache.solr.client.solrj.beans.Field;

import com.cn.leedane.model.RecordTimeBean;
import com.cn.leedane.mybatis.table.annotation.Column;

/**
 * 用户购买的商品实体bean 
 * @author LeeDane
 * 2017年10月25日 下午3:34:06
 * version 1.0
 */
public class S_UserBuyProductBean extends RecordTimeBean{
	
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
	 * 商品id
	 */
	@Column("product_id")
	@Field
	private int productId;

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}
	
	
}
