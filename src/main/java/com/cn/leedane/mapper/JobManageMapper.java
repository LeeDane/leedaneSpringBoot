package com.cn.leedane.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.cn.leedane.model.JobManageBean;

/**
 * 任务管理mapper接口类
 * @author LeeDane
 * 2017年6月5日 下午3:36:59
 * version 1.0
 */
public interface JobManageMapper extends BaseMapper<JobManageBean>{
	/**
	 * 判断记录是否存在
	 * @param name
	 * @param group
	 * @return
	 */
	public List<Map<String, Object>> isExist(@Param("name")String name, @Param("group")String group, @Param("status")int status);
	
	/**
	 * 分页获取任务列表
	 * @param start
	 * @param pageSize
	 * @return
	 */
	public List<Map<String, Object>> paging(@Param("start")int start, @Param("pageSize")int pageSize, @Param("status") int status);
}
