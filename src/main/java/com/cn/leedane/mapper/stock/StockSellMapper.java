package com.cn.leedane.mapper.stock;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cn.leedane.mapper.BaseMapper;
import com.cn.leedane.model.stock.StockSellBean;

/**
 * 股票卖出记录mapper接口类
 * @author LeeDane
 * 2018年6月21日 下午3:45:26
 * version 1.0
 */
public interface StockSellMapper extends BaseMapper<StockSellBean>{
	
	/**
	 * 获取用户的股票卖出记录列表
	 * @param userId
	 * @return
	 */
	public List<StockSellBean> getStockSells(
			@Param("createUserId") long userId,
			@Param("stockBuyId") long stockBuyId,
			@Param("status") int status);
}
