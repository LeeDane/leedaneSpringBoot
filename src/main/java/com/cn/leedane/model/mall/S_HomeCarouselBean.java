package com.cn.leedane.model.mall;

import org.apache.solr.client.solrj.beans.Field;

import com.cn.leedane.model.RecordTimeBean;
import com.cn.leedane.mybatis.table.annotation.Column;
import com.cn.leedane.mybatis.table.annotation.Table;

/**
 * 首页轮播商品实体bean 
 * @author LeeDane
 * 2017年12月26日 下午2:29:49
 * version 1.0
 */
@Table(value="t_mall_home_carousel")
public class S_HomeCarouselBean extends RecordTimeBean{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//状态：0：禁用， 1：正常
	
	
	@Column(value="product_id")
	@Field
	private long productId;
	
	@Column(value="carousel_order")
	@Field
	private int order;
	
	/**
	 * 展示图片的路径
	 */
	@Column(value="img")
	@Field
	private String img; 
	
	@Column(required=false)
	private S_ProductBean productBean;

	public long getProductId() {
		return productId;
	}

	public void setProductId(long productId) {
		this.productId = productId;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public S_ProductBean getProductBean() {
		return productBean;
	}

	public void setProductBean(S_ProductBean productBean) {
		this.productBean = productBean;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}
	
}
