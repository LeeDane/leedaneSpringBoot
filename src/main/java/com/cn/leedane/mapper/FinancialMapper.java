package com.cn.leedane.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.cn.leedane.model.FinancialBean;

/**
 * 记账相关的mapper接口类
 * @author LeeDane
 * 2016年7月22日 上午9:03:44
 * Version 1.0
 */
public interface FinancialMapper extends BaseMapper<FinancialBean>{

	/**
	 * 获取该用户指定年份的全部记账数据
	 * @param year
	 * @param status
	 * @param userId
	 * @return
	 */
	public List<Map<String, Object>> getLimit(@Param("year") int year, @Param("status")int status, @Param("userId")long userId);
	
	/**
	 * 获取该用户的全部记账数据
	 * @param status
	 * @param userId
	 * @return
	 */
	public List<Map<String, Object>> getAll(@Param("status")int status, @Param("userId")long userId);

	/**
	 * 获取指定用户的范围内的记账记录
	 * @param createUserId
	 * @param status
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public List<FinancialBean> getByTimeRange(@Param("createUserId")long createUserId, @Param("status")int status, @Param("startTime")Date startTime, @Param("endTime")Date endTime);
	
}
