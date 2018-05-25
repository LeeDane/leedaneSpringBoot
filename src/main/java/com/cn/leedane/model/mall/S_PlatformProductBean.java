package com.cn.leedane.model.mall;

import com.cn.leedane.model.IDBean;


/**
 * 第三方平台上的商品的bean
 * @author LeeDane
 * 2017年12月24日 下午9:26:05
 * version 1.0
 */
public class S_PlatformProductBean extends IDBean{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	private String title; //标题
	private String img; //主图片(取第一张)
	private double cashBackRatio; //比率
	private double cashBack; // 佣金
	private double price; //当前的价格
	private double oldPrice; //原先的价格
	private long auctionId; //唯一id
	private String platform; //平台
	private String shopTitle; //商铺名称
	private int couponAmount; //优惠券的金额
	private long couponLeftCount; //优惠券的剩余数量
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
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
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public long getAuctionId() {
		return auctionId;
	}
	public void setAuctionId(long auctionId) {
		this.auctionId = auctionId;
	}
	public String getPlatform() {
		return platform;
	}
	public void setPlatform(String platform) {
		this.platform = platform;
	}
	public String getShopTitle() {
		return shopTitle;
	}
	public void setShopTitle(String shopTitle) {
		this.shopTitle = shopTitle;
	}
	public double getOldPrice() {
		return oldPrice;
	}
	public void setOldPrice(double oldPrice) {
		this.oldPrice = oldPrice;
	}
	public int getCouponAmount() {
		return couponAmount;
	}
	public void setCouponAmount(int couponAmount) {
		this.couponAmount = couponAmount;
	}
	public long getCouponLeftCount() {
		return couponLeftCount;
	}
	public void setCouponLeftCount(long couponLeftCount) {
		this.couponLeftCount = couponLeftCount;
	}
	
	
}
