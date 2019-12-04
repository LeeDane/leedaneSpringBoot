package com.cn.leedane.model.mall.promotion;

import org.apache.solr.client.solrj.beans.Field;

import com.cn.leedane.model.RecordTimeBean;
import com.cn.leedane.mybatis.table.annotation.Column;
import com.cn.leedane.mybatis.table.annotation.Table;

/**
 * 推广商品的实体bean
 * @author LeeDane
 * 2018年3月26日 下午3:29:07
 * version 1.0
 */
@Table(value="t_mall_promotion_product")
public class S_PromotionProductBean extends RecordTimeBean{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 推广商品的ID
	 */
	@Column(value="product_id")
	@Field
	private String productId;
	
	/**
	 * 推广商品的平台
	 */
	@Column(value="platform")
	@Field
	private String platform;
	
	
	/**
	 * 商品的标题
	 */
	@Column(value="title")
	@Field
	private String title;


	public String getProductId() {
		return productId;
	}


	public void setProductId(String productId) {
		this.productId = productId;
	}


	public String getPlatform() {
		return platform;
	}


	public void setPlatform(String platform) {
		this.platform = platform;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}
}
