package com.cn.leedane.model.shop;

import com.cn.leedane.model.RecordTimeBean;
import com.cn.leedane.mybatis.table.annotation.Column;
import com.cn.leedane.mybatis.table.annotation.Table;

/**
 * 商品订单实体bean
 * @author LeeDane
 * 2017年12月7日 下午10:50:32
 * version 1.0
 */
@Table(value="t_shop_order")
public class S_OrderBean extends RecordTimeBean{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//订单的状态：0：禁用， 1：已完成  2：删除  9:待结算佣金 10：待支付佣金 11：已支付佣金 
	
	/**
	 * 用于展示状态用的，不做数据库的存储
	 */
	@Column(required = false)
	private String statusText;
	
	/**
	 * 订单编号
	 */
	@Column("order_code")
	private String orderCode;

	/**
	 * 商品的编号的唯一ID
	 */
	@Column("product_code")
	private String productCode; 
	
	
	/**
	 * 下订单的日期
	 */
	@Column("order_date")
	private String orderDate;
	
	/**
	 * 描述的标题，可以为空，用于展示用的
	 */
	@Column("title")
	private String title;
	
	/**
	 * 平台
	 */
	@Column("platform")
	private String platform;
	
	/**
	 * 推荐人
	 */
	@Column("referrer")
	private String referrer;
	
	/**
	 * 价格(是订单处理完成的时候计算的)
	 */
	@Column("price")
	private float price;
	
	/**
	 * 返还比例(是订单处理完成的时候计算的)
	 */
	@Column("cash_back_ratio")
	private float cashBackRatio;
	
	
	/**
	 * 佣金(是订单处理完成的时候计算的)
	 */
	@Column("cash_back")
	private float cashBack;

	public String getStatusText() {
		return statusText;
	}

	public void setStatusText(String statusText) {
		this.statusText = statusText;
	}

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getReferrer() {
		return referrer;
	}

	public void setReferrer(String referrer) {
		this.referrer = referrer;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public float getCashBackRatio() {
		return cashBackRatio;
	}

	public void setCashBackRatio(float cashBackRatio) {
		this.cashBackRatio = cashBackRatio;
	}

	public float getCashBack() {
		return cashBack;
	}

	public void setCashBack(float cashBack) {
		this.cashBack = cashBack;
	}

	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}
	
	
}
