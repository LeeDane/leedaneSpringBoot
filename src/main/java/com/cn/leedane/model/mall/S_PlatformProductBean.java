package com.cn.leedane.model.mall;

import com.cn.leedane.model.IDBean;
import com.cn.leedane.utils.MoneyUtil;


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
	private String shareUrl;//商品推广的地址
	private String couponShareUrl;//优惠券推广地址
	private int categoryId; //分类ID
	private long createUserId; //创建人
	private String subtitle;//子标题
	private String detail;//详情
	private Double afterCouponPrice;//券后价格

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
		if(cashBackRatio > 0.0d){
			return MoneyUtil.twoDecimalPlaces((price - couponAmount) * cashBackRatio / 100);
		}
		return cashBack;
	}
	public void setCashBack(double cashBack) {
		this.cashBack = cashBack;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = MoneyUtil.twoDecimalPlaces(price);
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
		this.oldPrice = MoneyUtil.twoDecimalPlaces(oldPrice);
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

	public String getShareUrl() {
		return shareUrl;
	}

	public void setShareUrl(String shareUrl) {
		this.shareUrl = shareUrl;
	}

	public String getCouponShareUrl() {
		return couponShareUrl;
	}

	public void setCouponShareUrl(String couponShareUrl) {
		this.couponShareUrl = couponShareUrl;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public long getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(long createUserId) {
		this.createUserId = createUserId;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public Double getAfterCouponPrice() {
		return afterCouponPrice;
	}

	public void setAfterCouponPrice(Double afterCouponPrice) {
		this.afterCouponPrice = afterCouponPrice;
	}
}
