package com.cn.leedane.model.mall;

import org.apache.solr.client.solrj.beans.Field;

import com.cn.leedane.model.RecordTimeBean;
import com.cn.leedane.mybatis.table.annotation.Column;
import com.cn.leedane.mybatis.table.annotation.Table;
import com.cn.leedane.utils.EnumUtil;

/**
 * 商品实体bean 
 * @author LeeDane
 * 2017年10月25日 下午3:00:37
 * version 1.0
 */
@Table(value="t_mall_product")
public class S_ProductBean extends RecordTimeBean{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//商品的状态：0：禁用， 1：正常
	
	/**
	 * 商品是否是最新的价格(因为商品的价格是随机变动的，而系统无法及时进行更新，为了查询方便，需要对商品
	 * 设置为是否是最新的，方便查询，当新增新的商品的时候，该商品旧的记录将被标记为false)
	 */
	@Column(value="is_new")
	@Field
	private boolean isNew; 
	
	/**
	 * 商品编号
	 */
	@Column(value="p_code")
	@Field
	private String code;
	
	/**
	 * 商品的标题
	 */
	@Field
	private String title;
	
	/**
	 * 商品的副标题
	 */
	@Field
	private String subtitle;
	
	/**
	 * 商品的摘要信息
	 */
	@Field
	private String digest;
	
	/**
	 * 商品的详情
	 */
	@Field
	private String detail;
	
	/**
	 * 商品的详情
	 */
	@Column(value="detail_source")
	@Field
	private String detailSource;
	
	/**
	 * 商品来源的平台(通过枚举赋值)
	 */
	@Field
	private String platform;
	
	/**
	 * 商品现价
	 */
	@Field
	private double price;
	
	/**
	 * 商品原价
	 */
	@Column(value="old_price")
	@Field
	private double oldPrice;
	
	/**
	 * 商品总的返现比率(百分比)
	 */
	@Column(value="cash_back_ratio")
	@Field
	private double cashBackRatio;
	
	/**
	 * 商品返现的价钱
	 */
	@Column(value="cash_back")
	@Field
	private double cashBack;
	
	/**
	 * 商店的ID
	 */
	@Column(value="shop_id")
	@Field
	private int shopId; 
	
	/**
	 * 商品的对应商店的实体(不做mybatis缓存处理和不在这里做solr处理，只做展示)
	 */
	@Column(required = false)
	private S_ShopBean shop; 
	
	/**
	 * 商品的链接
	 */
	@Field
	private String link;
	
	/**
	 * 商品的主图片链接，多个用;隔开
	 */
	@Column(value="main_img_links")
	@Field
	private String mainImgLinks;
	
	/**
	 * 分类ID，外键${CategoryBean} 的id
	 */
	@Column(value="category_id")
	@Field
	private int categoryId;
	
	/**
	 * 分类名称，冗余字段，是CategoryId对应的名称，只做展示用，数据实体表不做存储
	 */
	@Column(required = false)
	@Field
	private String category;

	/**
	 * 优惠券的金额
	 */
	@Column(value="coupon_amount")
	@Field
	private int couponAmount;

	/**
	 * 优惠券的地址
	 */
	@Column(value="coupon_url")
	@Field
	private String couponUrl;

	public S_ProductBean() {
		this.platform = EnumUtil.ProductPlatformType.淘宝.value; //默认是淘宝平台
	}

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDigest() {
		return digest;
	}

	public void setDigest(String digest) {
		this.digest = digest;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
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

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public double getOldPrice() {
		return oldPrice;
	}

	public void setOldPrice(double oldPrice) {
		this.oldPrice = oldPrice;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getMainImgLinks() {
		return mainImgLinks;
	}

	public void setMainImgLinks(String mainImgLinks) {
		this.mainImgLinks = mainImgLinks;
	}

	public String getDetailSource() {
		return detailSource;
	}

	public void setDetailSource(String detailSource) {
		this.detailSource = detailSource;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public int getShopId() {
		return shopId;
	}

	public void setShopId(int shopId) {
		this.shopId = shopId;
	}

	public S_ShopBean getShop() {
		return shop;
	}

	public void setShop(S_ShopBean shop) {
		this.shop = shop;
	}

	public int getCouponAmount() {
		return couponAmount;
	}

	public void setCouponAmount(int couponAmount) {
		this.couponAmount = couponAmount;
	}

	public String getCouponUrl() {
		return couponUrl;
	}

	public void setCouponUrl(String couponUrl) {
		this.couponUrl = couponUrl;
	}
}
