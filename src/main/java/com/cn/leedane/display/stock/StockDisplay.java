package com.cn.leedane.display.stock;

import java.util.List;


/**
 * 股票的处理
 * @author LeeDane
 * 2018年6月21日 下午4:24:02
 * version 1.0
 */
public class StockDisplay{
	
	private int id;
	
	private String name;
	
	private String code;
	
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
	 * 操作时间(创建时间)
	 */
	private String createTime;
	
	/**
	 * 所有的购买记录，按照时间的倒序排列
	 */
	private List<StockBuyDisplay> buys;

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

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public List<StockBuyDisplay> getBuys() {
		return buys;
	}

	public void setBuys(List<StockBuyDisplay> buys) {
		this.buys = buys;
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
