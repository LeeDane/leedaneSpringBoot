package com.cn.leedane.model.mall;

import org.apache.solr.client.solrj.beans.Field;

import com.cn.leedane.model.RecordTimeBean;
import com.cn.leedane.mybatis.table.annotation.Column;
import com.cn.leedane.mybatis.table.annotation.Table;

/**
 * 商品心愿单实体bean
 * @author LeeDane
 * 2017年11月13日 下午5:45:05
 * version 1.0
 */
@Table(value="t_mall_wish")
public class S_WishBean extends RecordTimeBean{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//心愿单的状态：0：禁用， 1：正常
	
	/**
	 * 商品id
	 */
	@Column("product_id")
	@Field
	private long productId;

	public long getProductId() {
		return productId;
	}

	public void setProductId(long productId) {
		this.productId = productId;
	}
	
	
}
