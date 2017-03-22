package com.cn.leedane.model;


/**
 * 购物车明细实体类
 * @author LeeDane
 * 2016年7月12日 上午10:38:25
 * Version 1.0
 */
//@Table(name="T_CART_DETAIL")
public class CartDetailsBean extends IDBean{
		
	private static final long serialVersionUID = 1L;

	/**
	 * 购物车
	 * 一对多的双向映射，在获取商品明细的时候就能获取所有相关联的购物车的信息
	 */
	private CartBean cart;
	
	/**
	 * 商品，是一对一的关系
	 */
	private ProductBean product;
	
	/**
	 * 商品的名称
	 */
	private String name;
	
	/**
	 * 现在的价格
	 */
	private float price;
	
	/**
	 * 原来的价格(原始的价格/原价)
	 */
	private float originPrice;
	
	/**
	 * 购物车选中该商品的数量
	 */
	private int number;
	
	/**
	 * 当前的库存库存(当小于1时，将显示无货，当小于10时将显示紧张)
	 */
	private int inventory;
	
	/**
	 * 该商品的总价(标价)
	 */
	private float totalPrice;
	
	/**
	 * 商家,多对一的关系
	 */
	private UserBean createUser;
	
	//@ManyToOne(cascade=CascadeType.ALL)
	public CartBean getCart() {
		return cart;
	}
	public void setCart(CartBean cart) {
		this.cart = cart;
	}
	
	//@OneToOne(targetEntity=ProductBean.class)  //mappedBy 以ProductBean中的配置
	//@JoinColumn(name="product_id", referencedColumnName="id")
	public ProductBean getProduct() {
		return product;
	}
	public void setProduct(ProductBean product) {
		this.product = product;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public int getInventory() {
		return inventory;
	}
	public void setInventory(int inventory) {
		this.inventory = inventory;
	}
	
	//@Column(name="total_price")
	public float getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(float totalPrice) {
		this.totalPrice = totalPrice;
	}
	
	
	//@ManyToOne(targetEntity=UserBean.class)
	//@JoinColumn(name="create_user_id", referencedColumnName="id",nullable=false)
	public UserBean getCreateUser() {
		return createUser;
	}
	public void setCreateUser(UserBean createUser) {
		this.createUser = createUser;
	}
	
}
