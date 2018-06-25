package com.cn.leedane.display.stock;




/**
 * 股票卖出记录的处理
 * @author LeeDane
 * 2018年6月21日 下午4:28:33
 * version 1.0
 */
public class StockSellDisplay{
	/**
	 * 股票的id
	 */
	private int stockId;
	
	/**
	 * 股票购买记录的id
	 */
	private int stockBuyId;
	
	/**
	 * 股票卖出记录的id
	 */
	private int id;
	
	/**
	 * 卖出价格
	 */
	private float price;
	
	/**
	 * 卖出数量
	 */
	private int number;
	
	/**
	 * 卖出剩余数量(为0表示已经卖完)
	 */
	private int residueNumber;
	
	/**
	 * 操作时间(创建时间)
	 */
	private String createTime;
	
	/**
	 * 卖出时间
	 */
	private String time;
	
	/**
	 * 卖出时间
	 */
	private String modifyDate;
	
	/**
	 * 卖出时间
	 */
	private String modifyTime;

	public int getStockId() {
		return stockId;
	}

	public void setStockId(int stockId) {
		this.stockId = stockId;
	}

	public int getStockBuyId() {
		return stockBuyId;
	}

	public void setStockBuyId(int stockBuyId) {
		this.stockBuyId = stockBuyId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
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
