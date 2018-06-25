package com.cn.leedane.model.stock;

import java.io.Serializable;
import java.util.List;

/**
 * 该股票买入的所有卖出记录实体bean
 * @author LeeDane
 * 2018年6月21日 下午3:51:09
 * version 1.0
 */
public class StockSellBeans implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<StockSellBean> stockSells;

	public List<StockSellBean> getStockSells() {
		return stockSells;
	}

	public void setStockSells(List<StockSellBean> stockSells) {
		this.stockSells = stockSells;
	}
}
