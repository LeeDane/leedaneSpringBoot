package com.cn.leedane.display.stock;

import java.util.List;



/**
 * 股票购买记录的处理
 * @author LeeDane
 * 2018年6月21日 下午4:25:33
 * version 1.0
 */
public class StockBuyDisplay{
	
	/**
	 * 股票的id
	 */
	private int stockId;
	
	/**
	 * 股票购买的id
	 */
	private int id;
	
	/**
	 * 买入价格
	 */
	private float price;
	
	/**
	 * 买入数量
	 */
	private int number;
	
	/**
	 * 当前持股数量
	 */
	private int holding;
	
	/**
	 * 是否卖光了
	 */
	private boolean isSellOut;
	
	/**
	 * 操作时间(创建时间)
	 */
	private String createTime;
	
	/**
	 * 购买时间
	 */
	private String time;
	
	/**
	 * 购买时间
	 */
	private String modifyDate;
	
	/**
	 * 购买时间
	 */
	private String modifyTime;
	
	/**
	 * 所有的卖出记录，按照时间的倒序排列
	 */
	private List<StockSellDisplay> sells;

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
		return isSellOut;
	}

	public void setSellOut(boolean isSellOut) {
		this.isSellOut = isSellOut;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public List<StockSellDisplay> getSells() {
		return sells;
	}

	public void setSells(List<StockSellDisplay> sells) {
		this.sells = sells;
	}

	public int getHolding() {
		return holding;
	}

	public void setHolding(int holding) {
		this.holding = holding;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(String modifyDate) {
		this.modifyDate = modifyDate;
	}

	public String getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(String modifyTime) {
		this.modifyTime = modifyTime;
	}
	
}
