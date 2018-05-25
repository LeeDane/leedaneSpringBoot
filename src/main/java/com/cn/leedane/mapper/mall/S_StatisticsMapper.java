package com.cn.leedane.mapper.mall;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cn.leedane.mapper.BaseMapper;
import com.cn.leedane.model.mall.S_StatisticsBean;

/**
 * 商品统计mapper接口类
 * @author LeeDane
 * 2017年11月13日 下午3:39:56
 * version 1.0
 */
public interface S_StatisticsMapper extends BaseMapper<S_StatisticsBean>{
	
	
	/**
	 * 获取相同
	 * @param productId
	 * @param statisticsDate
	 * @param statisticsType
	 * @return
	 */
	public List<S_StatisticsBean> findRecord(
			@Param("productId") int productId, 
			@Param("statisticsDate") String statisticsDate);
	
	/**
	 * 获取商品的统计
	 * @param start
	 * @param end
	 * @param productId
	 */
	public List<S_StatisticsBean> getRange(
			@Param("start") String start, 
			@Param("end") String end, 
			@Param("productId") int productId,
			@Param("status") int status);
	
}
