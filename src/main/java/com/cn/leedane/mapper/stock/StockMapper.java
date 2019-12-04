package com.cn.leedane.mapper.stock;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cn.leedane.mapper.BaseMapper;
import com.cn.leedane.model.stock.StockBean;

/**
 * 股票mapper接口类
 * @author LeeDane
 * 2018年6月21日 下午2:46:54
 * version 1.0
 */
public interface StockMapper extends BaseMapper<StockBean>{
	/**
	 * 获取用户的股票列表
	 * @param userId
	 * @return
	 */
	public List<StockBean> getStocks(@Param("createUserId") long userId, @Param("status") int status);
}
