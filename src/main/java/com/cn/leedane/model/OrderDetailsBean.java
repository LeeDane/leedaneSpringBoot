package com.cn.leedane.model;


/**
 * 订单明细实体类
 * @author LeeDane
 * 2016年7月12日 上午10:50:47
 * Version 1.0
 */
//@Table(name="T_ORDER_DETAIL")
public class OrderDetailsBean extends IDBean{
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * 商品的名称
	 */
	private String name;
	
	/**
	 * 现在的价格
	 */
	private float price;
	
	/**
	 * 原来的价格
	 */
	private float originPrice;
	
	/**
	 * 标价
	 */
	private float totalPrice;
	
	/**
	 * 商品的库存(当小于1时，将显示无货，当小于10时将显示紧张)
	 */
	private int inventory; 
	
	/**
	 * 商家，多对一的关系(不为空)
	 */
	private UserBean creater;
	
	/**
	 * 对应订单的id,是多对一的关系
	 */
	private OrderBean order;
	
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
	public int getInventory() {
		return inventory;
	}
	public void setInventory(int inventory) {
		this.inventory = inventory;
	}
	
	//@ManyToOne(targetEntity=UserBean.class,fetch=FetchType.EAGER,cascade=CascadeType.ALL)
	//@JoinColumn(name="user_id", referencedColumnName="id",nullable=false)
	public UserBean getCreater() {
		return creater;
	}
	public void setCreater(UserBean creater) {
		this.creater = creater;
	}
	
	//@ManyToOne(targetEntity=OrderBean.class,fetch=FetchType.EAGER,cascade=CascadeType.ALL)
	//@JoinColumn(name="order_id", referencedColumnName="id",nullable=false)
	public OrderBean getOrder() {
		return order;
	}
	public void setOrder(OrderBean order) {
		this.order = order;
	}
	public float getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(float totalPrice) {
		this.totalPrice = totalPrice;
	}
	
}
