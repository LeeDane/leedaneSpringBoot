package com.cn.leedane.mapper;

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
	
	public List<Map<String, Object>> visitors(
			@Param("user_id")int userId, 
			@Param("table_name")String tableName,
			@Param("table_id")int tableId,
			@Param("start")int start,
			@Param("page_size")int pageSize,
			@Param("status") int status);
}
