package com.cn.leedane.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.cn.leedane.model.VisitorBean;

/**
 * 访客mapper接口类
 * @author LeeDane
 * 2017年5月11日 下午4:45:42
 * version 1.0
 */
public interface VisitorMapper extends BaseMapper<VisitorBean>{
	
	/**
	 * 获取访问者
	 * @param tableName
	 * @param tableId
	 * @param start
	 * @param pageSize
	 * @param status
	 * @return
	 */
	public List<Map<String, Object>> visitors(
			@Param("tableName")String tableName,
			@Param("tableId")int tableId,
			@Param("start")int start,
			@Param("pageSize")int pageSize,
			@Param("status") int status);
	
	/**
	 * 获取某tableName(类型)最近有过访问记录的数据
	 * @param time
	 * @param tableName
	 * @param start
	 * @param pageSize
	 * @param status
	 * @return
	 */
	public List<Map<String, Object>> getRecently(
			@Param("time")Date time,
			@Param("tableName")String tableName,
			@Param("start")int start,
			@Param("pageSize")int pageSize,
			@Param("status") int status);
}
