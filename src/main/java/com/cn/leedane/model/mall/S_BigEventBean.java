package com.cn.leedane.model.mall;

import org.apache.solr.client.solrj.beans.Field;

import com.cn.leedane.model.RecordTimeBean;
import com.cn.leedane.mybatis.table.annotation.Column;
import com.cn.leedane.mybatis.table.annotation.Table;

/**
 * 商品大事件实体bean 
 * @author LeeDane
 * 2017年11月10日 上午11:10:20
 * version 1.0
 */
@Table(value="t_mall_big_event")
public class S_BigEventBean extends RecordTimeBean{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//商品的状态：0：禁用， 1：正常
	
	/**
	 * 商品的ID
	 */
	@Column(value="product_id")
	@Field
	private int productId;
	
	
	/**
	 * 大事件的文本信息
	 */
	@Column(value="text")
	@Field
	private String text;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	} 
	
	
	
}
