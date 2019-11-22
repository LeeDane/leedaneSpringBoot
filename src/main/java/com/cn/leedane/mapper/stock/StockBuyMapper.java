package com.cn.leedane.mapper.stock;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cn.leedane.mapper.BaseMapper;
import com.cn.leedane.model.stock.StockBuyBean;

/**
 * 股票购买记录mapper接口类
 * @author LeeDane
 * 2018年6月21日 下午3:14:10
 * version 1.0
 */
public interface StockBuyMapper extends BaseMapper<StockBuyBean>{
	
	/**
	 * 获取用户的股票购买记录
	 * @param userId
	 * @return
	 */
	@Deprecated
	public StockBuyBean getStockBuy(
			@Param("createUserId") long userId,
			@Param("stockId") long stockId,
			@Param("status") int status);
	
	/**
	 * 获取用户的股票购买记录列表
	 * @param userId
	 * @return
	 */
	public List<StockBuyBean> getStockBuys(
			@Param("createUserId") long userId,
			@Param("stockId") long stockId,
			@Param("status") int status);
}
