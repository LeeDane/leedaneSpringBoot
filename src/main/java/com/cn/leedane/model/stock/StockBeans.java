package com.cn.leedane.model.stock;

import java.io.Serializable;
import java.util.List;

/**
 * 所有的股票实体bean
 * @author LeeDane
 * 2018年6月21日 下午2:59:14
 * version 1.0
 */
public class StockBeans implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<StockBean> stocks;

	public List<StockBean> getStocks() {
		return stocks;
	}

	public void setStocks(List<StockBean> stocks) {
		this.stocks = stocks;
	}
}
