package com.cn.leedane.model.mall;

import com.cn.leedane.model.RecordTimeBean;
import com.cn.leedane.mybatis.table.annotation.Column;
import com.cn.leedane.mybatis.table.annotation.Table;

import java.util.Date;

/**
 * 商品订单详情实体bean（对接各个平台的订单记录）
 * @author LeeDane
 * 2019年12月7日 下午10:50:32
 * version 1.0
 */
@Table(value="t_mall_order_detail")
public class S_OrderDetailBean extends RecordTimeBean{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//订单的状态：0：禁用， 1:正常 STATUS_SETTLEMENT：已结算  STATUS_PAY：已支付 STATUS_END：平台已经处理结束

	/**
	 * 订单实际创建时间
	 */
	@Column("order_create_time")
	private Date orderCreateTime;

	/**
	 * 订单实际支付时间
	 */
	@Column("order_pay_time")
	private Date orderPayTime;

	/**
	 * 订单结算时间，只有状态是结算状态才有结算时间
	 */
	@Column("order_settlement_time")
	private Date orderSettlementTime;

	/**
	 * 产品ID
	 */
	@Column("order_product_id")
	private long orderProductId;

	/**
	 * 订单编号
	 */
	@Column("order_code")
	private long orderCode;

	/**
	 * 订单子编号
	 */
	@Column("order_sub_code")
	private long orderSubCode;

	/**
	 * 订单分类
	 */
	@Column("order_category")
	private String orderCategory;

	/**
	 * 订单类型，如：聚划算/天猫/淘宝等
	 */
	@Column("order_type")
	private String orderType;

	/**
	 * 订单实际支付金额
	 */
	@Column("order_pay_money")
	private double orderPayMoney;

	/**
	 * 订单实际结算金额，只要订单是结算状态才有值
	 */
	@Column("order_settlement_money")
	private double orderSettlementMoney;

	/**
	 * 订单商品购买数量，默认是1
	 */
	@Column("order_buy_numer")
	private int orderBuyNumer;

	/**
	 * 订单平台，如：淘宝网/京东/拼多多
	 */
	@Column("platform")
	private String platform;

	/**
	 * 订单预估佣金比率，百分比
	 */
	@Column("order_cash_back_ratio")
	private double orderCashBackRatio;

	/**
	 * 订单预估佣金金额
	 */
	@Column("order_cash_back")
	private double orderCashBack;

	/**
	 * 最终结算给平台用户依据这个
	 * 订单结算佣金金额(只有结算状态才会大于0.0),最终系统收到平台的钱
	 */
	@Column("order_settlement_cash_back")
	private double orderSettlementCashBack;


	/**
	 * 商品的标题
	 */
	@Column("product_title")
	private String productTitle;

	public Date getOrderCreateTime() {
		return orderCreateTime;
	}

	public void setOrderCreateTime(Date orderCreateTime) {
		this.orderCreateTime = orderCreateTime;
	}

	public Date getOrderPayTime() {
		return orderPayTime;
	}

	public void setOrderPayTime(Date orderPayTime) {
		this.orderPayTime = orderPayTime;
	}

	public Date getOrderSettlementTime() {
		return orderSettlementTime;
	}

	public void setOrderSettlementTime(Date orderSettlementTime) {
		this.orderSettlementTime = orderSettlementTime;
	}

	public long getOrderProductId() {
		return orderProductId;
	}

	public void setOrderProductId(long orderProductId) {
		this.orderProductId = orderProductId;
	}

	public long getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(long orderCode) {
		this.orderCode = orderCode;
	}

	public long getOrderSubCode() {
		return orderSubCode;
	}

	public void setOrderSubCode(long orderSubCode) {
		this.orderSubCode = orderSubCode;
	}

	public String getOrderCategory() {
		return orderCategory;
	}

	public void setOrderCategory(String orderCategory) {
		this.orderCategory = orderCategory;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public double getOrderPayMoney() {
		return orderPayMoney;
	}

	public void setOrderPayMoney(double orderPayMoney) {
		this.orderPayMoney = orderPayMoney;
	}

	public double getOrderSettlementMoney() {
		return orderSettlementMoney;
	}

	public void setOrderSettlementMoney(double orderSettlementMoney) {
		this.orderSettlementMoney = orderSettlementMoney;
	}

	public int getOrderBuyNumer() {
		return orderBuyNumer;
	}

	public void setOrderBuyNumer(int orderBuyNumer) {
		this.orderBuyNumer = orderBuyNumer;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public double getOrderCashBackRatio() {
		return orderCashBackRatio;
	}

	public void setOrderCashBackRatio(double orderCashBackRatio) {
		this.orderCashBackRatio = orderCashBackRatio;
	}

	public double getOrderCashBack() {
		return orderCashBack;
	}

	public void setOrderCashBack(double orderCashBack) {
		this.orderCashBack = orderCashBack;
	}

	public double getOrderSettlementCashBack() {
		return orderSettlementCashBack;
	}

	public void setOrderSettlementCashBack(double orderSettlementCashBack) {
		this.orderSettlementCashBack = orderSettlementCashBack;
	}

	public String getProductTitle() {
		return productTitle;
	}

	public void setProductTitle(String productTitle) {
		this.productTitle = productTitle;
	}
}
