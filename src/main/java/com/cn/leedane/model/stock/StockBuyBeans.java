package com.cn.leedane.model.stock;

import java.io.Serializable;
import java.util.List;

/**
 * 所有的股票购买记录实体bean
 * @author LeeDane
 * 2018年6月21日 下午3:18:20
 * version 1.0
 */
public class StockBuyBeans implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<StockBuyBean> stockBuys;

	public List<StockBuyBean> getStockBuys() {
		return stockBuys;
	}

	public void setStockBuys(List<StockBuyBean> stockBuys) {
		this.stockBuys = stockBuys;
	}
	
}
