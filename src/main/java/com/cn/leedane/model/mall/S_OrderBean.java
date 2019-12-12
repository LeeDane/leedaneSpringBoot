package com.cn.leedane.model.mall;

import com.cn.leedane.model.RecordTimeBean;
import com.cn.leedane.mybatis.table.annotation.Column;
import com.cn.leedane.mybatis.table.annotation.Table;

import java.util.Date;

/**
 * 商品订单实体bean
 * @author LeeDane
 * 2017年12月7日 下午10:50:32
 * version 1.0
 */
@Table(value="t_mall_order")
public class S_OrderBean extends RecordTimeBean{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//订单的状态：0：禁用， 1：已提交  STATUS_SETTLEMENT：已结算  STATUS_PAY：已支付 STATUS_END：平台已经处理结束
	
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
	 * 下单时间
	 */
	@Column("order_time")
	private Date orderTime;

	/**
	 * 付款时间
	 */
	@Column("pay_time")
	private Date payTime;

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
	 * 价格(是订单处理完成的时候计算的)
	 */
	@Column("price")
	private double price;
	
	/**
	 * 预计返还比例(是订单处理完成的时候计算的)
	 */
	@Column("expect_cash_back_ratio")
	private double cashBackRatio;
	
	
	/**
	 * 预计佣金(是订单处理完成的时候计算的)
	 */
	@Column("expect_cash_back")
	private double cashBack;

	/**
	 * 关联订单详情ID(只有合法的订单（当时是非议的订单），才能关联以及查看)
	 */
	@Column("order_detail_id")
	private long orderDetailId;

	/**
	 * 标记是否有争议，添加订单的时候判断是否存在相同的订单，有的话将双方的订单都标记为有争议状态
	 */
	/*@Column("dispute")
	private boolean dispute;*/

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

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public double getCashBackRatio() {
		return cashBackRatio;
	}

	public void setCashBackRatio(double cashBackRatio) {
		this.cashBackRatio = cashBackRatio;
	}

	public double getCashBack() {
		return cashBack;
	}

	public void setCashBack(double cashBack) {
		this.cashBack = cashBack;
	}

	public Date getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(Date orderTime) {
		this.orderTime = orderTime;
	}

	public Date getPayTime() {
		return payTime;
	}

	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}

	public long getOrderDetailId() {
		return orderDetailId;
	}

	public void setOrderDetailId(long orderDetailId) {
		this.orderDetailId = orderDetailId;
	}
}
