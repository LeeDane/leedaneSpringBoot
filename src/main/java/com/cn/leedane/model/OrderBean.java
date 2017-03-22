package com.cn.leedane.model;

import java.util.Date;
/**
 * 订单实体类
 * @author LeeDane
 * 2016年7月12日 上午10:49:29
 * Version 1.0
 */
//@Table(name="T_ORDER")
public class OrderBean extends RecordTimeBean {			

	private static final long serialVersionUID = 1L;
	//订单的状态，0：删除，1：正常，2：取消
	/**
	 * 买家姓名
	 */
	private String buyerName;
	
	/**
	 * 买家地址
	 */
	private String buyerAddress;
	
	/**
	 * 邮编
	 */
	private int mailCode;
	
	/**
	 * 联系电话
	 */
	private int phoneNumber;
	
	/**
	 * 付款状态,0:未支付 ：1：已经支付，2：支付异常
	 */
	private int isPay;
	
	/**
	 * 是否开发票 0：否，1：是
	 */
	private int isInvoice;
	
	/**
	 * 发票的抬头
	 */
	private String invoiceTitle;
	
	/**
	 * 发票的类型
	 */
	private int invoiceType;
	
	/**
	 * 发票内容
	 */
	private String invoiceContent;
	
	/**
	 * 发票有效时间
	 */
	private Date invoiceEffectiveTime;
	
	/**
	 * 预计送货的时间
	 */
	private Date deliveryTime;
	
	/**
	 * 买家期望的送货时间开始时间
	 */
	private Date buyerWantDeliveryFromTime;
	
	/**
	 * 买家期望的送货时间结束时间
	 */
	private Date buyerWantDeliveryToTime;
	
	/**
	 * 收货时间
	 */
	private Date goodsTime;
	
	/**
	 * 是否提前电话通知 0表示否，1表示是
	 */
	private int isPhoneBefore;
	
	/**
	 * 实际支付的金额
	 */
	private float money;  
	
	/**
	 * 原来的总价
	 */
	private float totalMoney;
	
	/**
	 * 备注信息
	 */
	private String note;
	
	/**
	 * 优惠信息
	 */
	private String preferential;
	
	//@Column(name="buyer_name")
	public String getBuyerName() {
		return buyerName;
	}
	public void setBuyerName(String buyerName) {
		this.buyerName = buyerName;
	}
	
	//@Column(name="buyer_address")
	public String getBuyerAddress() {
		return buyerAddress;
	}
	public void setBuyerAddress(String buyerAddress) {
		this.buyerAddress = buyerAddress;
	}
	
	//@Column(name="mail_code")
	public int getMailCode() {
		return mailCode;
	}
	public void setMailCode(int mailCode) {
		this.mailCode = mailCode;
	}
	
	//@Column(name="phone_number")
	public int getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(int phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	//@Column(columnDefinition="INT default 0",name="is_pay")  //设置默认值是0，表示未支付
	public int getIsPay() {
		return isPay;
	}
	public void setIsPay(int isPay) {
		this.isPay = isPay;
	}
	//@Column(columnDefinition="INT default 0",name="is_invoice")  //设置默认值是0，表示否
	public int getIsInvoice() {
		return isInvoice;
	}
	public void setIsInvoice(int isInvoice) {
		this.isInvoice = isInvoice;
	}
	
	//@Column(name="invoice_title")
	public String getInvoiceTitle() {
		return invoiceTitle;
	}
	public void setInvoiceTitle(String invoiceTitle) {
		this.invoiceTitle = invoiceTitle;
	}
	
	//@Column(name="invoice_type")
	public int getInvoiceType() {
		return invoiceType;
	}
	public void setInvoiceType(int invoiceType) {
		this.invoiceType = invoiceType;
	}
	
	//@Column(name="invoice_content")
	public String getInvoiceContent() {
		return invoiceContent;
	}
	public void setInvoiceContent(String invoiceContent) {
		this.invoiceContent = invoiceContent;
	}
		
	//@Column(name="invoice_effective_time")
	public Date getInvoiceEffectiveTime() {
		return invoiceEffectiveTime;
	}
	public void setInvoiceEffectiveTime(Date invoiceEffectiveTime) {
		this.invoiceEffectiveTime = invoiceEffectiveTime;
	}
	
	//@Column(name="delivery_time")
	public Date getDeliveryTime() {
		return deliveryTime;
	}
	public void setDeliveryTime(Date deliveryTime) {
		this.deliveryTime = deliveryTime;
	}
	
	//@Column(name="buyer_want_delivery_from_time")
	public Date getBuyerWantDeliveryFromTime() {
		return buyerWantDeliveryFromTime;
	}
	public void setBuyerWantDeliveryFromTime(Date buyerWantDeliveryFromTime) {
		this.buyerWantDeliveryFromTime = buyerWantDeliveryFromTime;
	}
		
	//@Column(name="buyer_want_delivery_to_time")
	public Date getBuyerWantDeliveryToTime() {
		return buyerWantDeliveryToTime;
	}
	public void setBuyerWantDeliveryToTime(Date buyerWantDeliveryToTime) {
		this.buyerWantDeliveryToTime = buyerWantDeliveryToTime;
	}
	
	//@Column(name="goods_time")
	public Date getGoodsTime() {
		return goodsTime;
	}
	public void setGoodsTime(Date goodsTime) {
		this.goodsTime = goodsTime;
	}
	
	//@Column(columnDefinition="INT default 0",name="is_phone_before")  //设置默认值是0，否
	public int getIsPhoneBefore() {
		return isPhoneBefore;
	}
	public void setIsPhoneBefore(int isPhoneBefore) {
		this.isPhoneBefore = isPhoneBefore;
	}
	public float getMoney() {
		return money;
	}
	public void setMoney(float money) {
		this.money = money;
	}
	public float getTotalMoney() {
		return totalMoney;
	}
	public void setTotalMoney(float totalMoney) {
		this.totalMoney = totalMoney;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getPreferential() {
		return preferential;
	}
	public void setPreferential(String preferential) {
		this.preferential = preferential;
	}
	
}
