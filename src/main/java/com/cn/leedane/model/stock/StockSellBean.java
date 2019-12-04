package com.cn.leedane.model.stock;

import org.apache.solr.client.solrj.beans.Field;

import com.cn.leedane.model.RecordTimeBean;
import com.cn.leedane.mybatis.table.annotation.Column;

/**
 * 股票卖出实体bean
 * @author LeeDane
 * 2018年6月21日 上午10:53:24
 * version 1.0
 */
public class StockSellBean extends RecordTimeBean{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//股票卖出的状态：0：禁用， 1：正常
	
	/**
	 * 股票买入的id
	 */
	@Column("stock_buy_id")
	@Field
	private int stockBuyId;
	
	/**
	 * 卖出价格
	 */
	@Field
	private float price;
	
	/**
	 * 卖出数量
	 */
	@Field
	private int number;
	
	/**
	 * 卖出剩余数量(为0表示已经卖完)
	 */
	@Column("residue_number")
	@Field
	private int residueNumber;

	public int getStockBuyId() {
		return stockBuyId;
	}

	public void setStockBuyId(int stockBuyId) {
		this.stockBuyId = stockBuyId;
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

	public int getResidueNumber() {
		return residueNumber;
	}

	public void setResidueNumber(int residueNumber) {
		this.residueNumber = residueNumber;
	}

}
