package com.cn.leedane.model.mall;

import org.apache.solr.client.solrj.beans.Field;

import com.cn.leedane.model.RecordTimeBean;
import com.cn.leedane.mybatis.table.annotation.Column;
import com.cn.leedane.mybatis.table.annotation.Table;

/**
 * 商城首页商店实体
 * @author LeeDane
 * 2018年1月11日 下午2:20:23
 * version 1.0
 */
@Table(value="t_mall_home_shop")
public class S_HomeShopBean extends RecordTimeBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//状态：0：禁用， 1：正常
	
	
		@Column(value="shop_id")
		@Field
		private long shopId;
		
		@Column(value="shop_order")
		@Field
		private int shopOrder;
		
		@Column(required=false)
		private S_ShopBean shopBean;

		public long getShopId() {
			return shopId;
		}

		public void setShopId(long shopId) {
			this.shopId = shopId;
		}

		public int getShopOrder() {
			return shopOrder;
		}

		public void setShopOrder(int shopOrder) {
			this.shopOrder = shopOrder;
		}

		public S_ShopBean getShopBean() {
			return shopBean;
		}

		public void setShopBean(S_ShopBean shopBean) {
			this.shopBean = shopBean;
		}
}
