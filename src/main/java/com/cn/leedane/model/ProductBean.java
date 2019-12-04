package com.cn.leedane.model;
import java.util.Date;
/**
 * 产品的实体类
 * @author LeeDane
 * 2016年7月12日 上午10:39:10
 * Version 1.0
 */
//@Table(name="T_PRODUCT")
public class ProductBean extends RecordTimeBean{

	private static final long serialVersionUID = 1L;
	
	//商品状态，0：已下架,1：在销售，2：预定中
	
	/**
	 * 商品名称
	 */
	private String name;
	
	/**
	 * 商品编号
	 */
	private String code;
	
	/**
	 * 商品排序号
	 */
	private String sequence;
	
	/**
	 * 商品描述
	 */
	private String desc;
	
	/**
	 * 商品数量，库存的数量
	 */
	private int number; 
	
	/**
	 * 商品规格，如：100*10*10
	 */
	private String standard;
	
	/**
	 * 商品大小
	 */
	private String size; 
	
	/**
	 * 商品颜色
	 */
	private String color;
	
	/**
	 * 商品重量
	 */
	private String weight;
	
	/**
	 * 商品价格(现价)
	 */
	private float price;
	
	/**
	 * 打折或者优惠之前的价格，有可能等于标价
	 */
	private float originPrice;
	
	/**
	 * 标价
	 */
	private float totalPrice;
	
	/**
	 * 商品折扣
	 */
	private float discount;
	
	/**
	 * 生产日期
	 */
	private Date productDate;
	
	/**
	 * 保质期
	 */
	private String shelfLife;
	
	/**
	 * 有效日期
	 */
	private Date effectiveDate;
	
	/**
	 * 产家
	 */
	private String producers;
	
	/**
	 * 产地
	 */
	private String area;
	
	/**
	 * 所属的品牌(公司)
	 */
	private CompanyBean company;
	
	/**
	 * 是否优惠，0:否，1：有优惠
	 */
	private int isPreferential;
	
	/**
	 * 优惠开始时间
	 */
	private Date preferentialFromTime;
	
	/**
	 * 优惠结束时间
	 */
	private Date preferentialToTime;
	
	/**
	 * 多个中间用“，”隔开，支持支付方式,0:在线支付(默认)，1：货到刷卡，2：货到现付，3：分期支付
	 */
	private String payType;
	
	/**
	 * 支持送货方式，0:网站自营，1：商家快递
	 */
	private int sellType;
	
	/**
	 * 点击次数
	 */
	private int clickNumber;
	
	/**
	 * 评论次数
	 */
	private int commentNumber;
		
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getSequence() {
		return sequence;
	}
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}
	
	//@Column(name="product_desc")
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public String getStandard() {
		return standard;
	}
	public void setStandard(String standard) {
		this.standard = standard;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getWeight() {
		return weight;
	}
	public void setWeight(String weight) {
		this.weight = weight;
	}
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	
	//@Column(name="origin_price")
	public float getOriginPrice() {
		return originPrice;
	}
	public void setOriginPrice(float originPrice) {
		this.originPrice = originPrice;
	}
	
	public float getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(float totalPrice) {
		this.totalPrice = totalPrice;
	}
	public float getDiscount() {
		return discount;
	}
	public void setDiscount(float discount) {
		this.discount = discount;
	}
	
	//@Column(name="product_date")
	public Date getProductDate() {
		return productDate;
	}
	public void setProductDate(Date productDate) {
		this.productDate = productDate;
	}
	
	//@Column(name="shelf_life")
	public String getShelfLife() {
		return shelfLife;
	}
	public void setShelfLife(String shelfLife) {
		this.shelfLife = shelfLife;
	}
	//@Column(name="effective_date")
	public Date getEffectiveDate() {
		return effectiveDate;
	}
	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}
	public String getProducers() {
		return producers;
	}
	public void setProducers(String producers) {
		this.producers = producers;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	
	//@Column(columnDefinition="INT default 0",name="is_preferential")  //设置默认值是0，0表示没优惠
	public int getIsPreferential() {
		return isPreferential;
	}
	public void setIsPreferential(int isPreferential) {
		this.isPreferential = isPreferential;
	}
	
	
	//@Column(name="preferential_from_time")
	public Date getPreferentialFromTime() {
		return preferentialFromTime;
	}
	public void setPreferentialFromTime(Date preferentialFromTime) {
		this.preferentialFromTime = preferentialFromTime;
	}
	
	//@Column(name="preferential_to_time")
	public Date getPreferentialToTime() {
		return preferentialToTime;
	}
	public void setPreferentialToTime(Date preferentialToTime) {
		this.preferentialToTime = preferentialToTime;
	}
	
	//@Column(name="pay_type")
	public String getPayType() {
		return payType;
	}
	public void setPayType(String payType) {
		this.payType = payType;
	}
	
	//@Column(columnDefinition="INT default 0",name="sell_type")  //设置默认值是0，0表示商城自营
	public int getSellType() {
		return sellType;
	}
	public void setSellType(int sellType) {
		this.sellType = sellType;
	}
	
	//@Column(name="click_number")
	public int getClickNumber() {
		return clickNumber;
	}
	public void setClickNumber(int clickNumber) {
		this.clickNumber = clickNumber;
	}
	
	//@Column(name="comment_number")
	public int getCommentNumber() {
		return commentNumber;
	}
	public void setCommentNumber(int commentNumber) {
		this.commentNumber = commentNumber;
	}
	
	//@ManyToOne(targetEntity = CompanyBean.class)
	//@JoinColumn(name="company_id", referencedColumnName="id", nullable=false)
	public CompanyBean getCompany() {
		return company;
	}
	public void setCompany(CompanyBean company) {
		this.company = company;
	}
	
}