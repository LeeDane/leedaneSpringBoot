package com.cn.leedane.mapper.clock;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cn.leedane.mapper.BaseMapper;
import com.cn.leedane.model.clock.ClockDynamicBean;

/**
 * 任务动态mapper接口类
 * @author LeeDane
 * 2018年11月23日 下午4:28:57
 * version 1.0
 */
public interface ClockDynamicMapper extends BaseMapper<ClockDynamicBean>{
	/**
	 * 获取任务动态列表
	 * @param clockId
	 * @param publicity  是否是公开，只有创建者才能查看公开的动态
	 * @param start
	 * @param pageSize
	 * @return
	 */
	public List<ClockDynamicBean> dynamics(
			@Param("clockId")int clockId, 
			@Param("publicity")boolean publicity, 
			@Param("start")int start, 
			@Param("pageSize")int pageSize);
}
