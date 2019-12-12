package com.cn.leedane.mall.model;

import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.RecordTimeBean;
import com.cn.leedane.model.StatusBean;
import com.cn.leedane.mybatis.table.annotation.Column;
import com.cn.leedane.mybatis.table.annotation.Table;
import com.github.liaochong.myexcel.core.annotation.ExcelColumn;
import org.apache.solr.client.solrj.beans.Field;

import java.util.Date;

/**
 * 商品订单详情实体bean（对接各个平台的订单记录）
 * @author LeeDane
 * 2019年12月7日 下午10:50:32
 * version 1.0
 */
public class S_OrderTaobaoDetailBean extends IDBean {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//订单的状态：0：禁用， 1:正常 STATUS_SETTLEMENT：已结算  STATUS_PAY：已支付 STATUS_END：平台已经处理结束

	/**
	 * 订单状态，需要做处理
	 */
	@ExcelColumn(title = "订单状态")
	protected String status;

	/**
	 * 订单实际创建时间
	 */
	@ExcelColumn(title = "创建时间")
	private Date orderCreateTime;

	/**
	 * 订单实际支付时间
	 */
	@ExcelColumn(title = "淘宝付款时间")
	private Date orderPayTime;

	/**
	 * 订单结算时间，只有状态是结算状态才有结算时间
	 */
	@ExcelColumn(title = "结算时间")
	private Date orderSettlementTime;

	/**
	 * 产品ID
	 */
	@ExcelColumn(title = "商品ID")
	private long orderProductId;

	/**
	 * 订单编号
	 */
	@ExcelColumn(title = "淘宝订单编号")
	private long orderCode;

	/**
	 * 订单子编号
	 */
	@ExcelColumn(title = "淘宝子订单号")
	private long orderSubCode;

	/**
	 * 订单分类
	 */
	@ExcelColumn(title = "类目名称")
	private String orderCategory;

	/**
	 * 订单类型，如：聚划算/天猫/淘宝等
	 */
	@ExcelColumn(title = "订单类型")
	private String orderType;

	/**
	 * 订单实际支付金额
	 */
	@ExcelColumn(title = "付款金额")
	private double orderPayMoney;

	/**
	 * 订单实际结算金额，只要订单是结算状态才有值
	 * 平台结算佣金的总金额
	 */
	@ExcelColumn(title = "结算金额")
	private double orderSettlementMoney;

	/**
	 * 订单商品购买数量，默认是1
	 */
	@ExcelColumn(title = "商品数量")
	private int orderBuyNumer;

	/**
	 * 订单预估佣金比率，百分比
	 */
	@ExcelColumn(title = "佣金比率")
	private String orderCashBackRatio;

	/**
	 * 订单预估佣金金额
	 */
	@ExcelColumn(title = "付款预估收入")
	private double orderCashBack;

	/**
	 * 最终结算给平台用户依据这个
	 * 订单结算佣金金额(只有结算状态才会大于0.0),最终系统收到平台的钱
	 */
	@ExcelColumn(title = "佣金金额")
	private double orderSettlementCashBack;

	/**
	 * 商品的标题
	 */
	@ExcelColumn(title = "商品标题")
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

	public String getOrderCashBackRatio() {
		return orderCashBackRatio;
	}

	public void setOrderCashBackRatio(String orderCashBackRatio) {
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getProductTitle() {
		return productTitle;
	}

	public void setProductTitle(String productTitle) {
		this.productTitle = productTitle;
	}
}
