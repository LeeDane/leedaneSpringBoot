package com.cn.leedane.mapper.shop;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.cn.leedane.mapper.BaseMapper;
import com.cn.leedane.model.shop.S_ProductBean;
import com.cn.leedane.model.shop.S_StatisticsBean;

/**
 * 商品统计mapper接口类
 * @author LeeDane
 * 2017年11月13日 下午3:39:56
 * version 1.0
 */
public interface S_StatisticsMapper extends BaseMapper<S_StatisticsBean>{
	/**
	 * 获取商品的统计
	 * @param start
	 * @param end
	 * @param productId
	 */
	public List<S_ProductBean> getRange(
			@Param("start") String start, 
			@Param("end") String end, 
			@Param("productId") int productId);
	
}
