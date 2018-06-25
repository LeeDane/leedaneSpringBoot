package com.cn.leedane.model.stock;

import org.apache.solr.client.solrj.beans.Field;

import com.cn.leedane.model.RecordTimeBean;
import com.cn.leedane.mybatis.table.annotation.Column;

/**
 * 股票购买实体bean
 * @author LeeDane
 * 2018年6月21日 上午10:49:50
 * version 1.0
 */
public class StockBuyBean extends RecordTimeBean{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//股票的状态：0：禁用， 1：正常
	
	/**
	 * 股票的id
	 */
	@Column("stock_id")
	@Field
	private int stockId;
	
	/**
	 * 买入价格
	 */
	@Field
	private float price;
	
	/**
	 * 买入数量
	 */
	@Field
	private int number;
	
	/**
	 * 是否卖光了
	 */
	@Column("sell_out")
	@Field
	private boolean sellOut;

	public int getStockId() {
		return stockId;
	}

	public void setStockId(int stockId) {
		this.stockId = stockId;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public boolean isSellOut() {
		return sellOut;
	}

	public void setSellOut(boolean sellOut) {
		this.sellOut = sellOut;
	}
	
}
