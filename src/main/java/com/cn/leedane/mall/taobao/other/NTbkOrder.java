package com.cn.leedane.mall.taobao.other;

import com.taobao.api.internal.mapping.ApiField;

import java.io.Serializable;
import java.util.Date;

public class NTbkOrder implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@ApiField("trade_parent_id")
	private long trade_parent_id;
	
	@ApiField("trade_id")
    private long trade_id;
	
	@ApiField("num_iid")
    private long num_iid;

	@ApiField("item_title")
	private String item_title;
    
	@ApiField("item_num")
	private int item_num;
     
	@ApiField("price")
    private float price;

	@ApiField("pay_price")
	private float pay_price;

	@ApiField("seller_nick")
    private String seller_nick;
     
	@ApiField("seller_shop_title")
	private String seller_shop_title;
     
	@ApiField("commission")
	private float commission;

	@ApiField("commission_rate")
	private float commission_rate;
    
	@ApiField("unid")
	private String unid;
    
	@ApiField("create_time")
	private Date create_time;
     
	@ApiField("earning_time")
	private Date earning_time;

	public long getTrade_parent_id() {
		return trade_parent_id;
	}

	public void setTrade_parent_id(long trade_parent_id) {
		this.trade_parent_id = trade_parent_id;
	}

	public long getTrade_id() {
		return trade_id;
	}

	public void setTrade_id(long trade_id) {
		this.trade_id = trade_id;
	}

	public long getNum_iid() {
		return num_iid;
	}

	public void setNum_iid(long num_iid) {
		this.num_iid = num_iid;
	}

	public String getItem_title() {
		return item_title;
	}

	public void setItem_title(String item_title) {
		this.item_title = item_title;
	}

	public int getItem_num() {
		return item_num;
	}

	public void setItem_num(int item_num) {
		this.item_num = item_num;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public float getPay_price() {
		return pay_price;
	}

	public void setPay_price(float pay_price) {
		this.pay_price = pay_price;
	}

	public String getSeller_nick() {
		return seller_nick;
	}

	public void setSeller_nick(String seller_nick) {
		this.seller_nick = seller_nick;
	}

	public String getSeller_shop_title() {
		return seller_shop_title;
	}

	public void setSeller_shop_title(String seller_shop_title) {
		this.seller_shop_title = seller_shop_title;
	}

	public float getCommission() {
		return commission;
	}

	public void setCommission(float commission) {
		this.commission = commission;
	}

	public float getCommission_rate() {
		return commission_rate;
	}

	public void setCommission_rate(float commission_rate) {
		this.commission_rate = commission_rate;
	}

	public String getUnid() {
		return unid;
	}

	public void setUnid(String unid) {
		this.unid = unid;
	}

	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}

	public Date getEarning_time() {
		return earning_time;
	}

	public void setEarning_time(Date earning_time) {
		this.earning_time = earning_time;
	}
	
	

}
