package com.cn.leedane.mapper.circle;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.cn.leedane.mapper.BaseMapper;
import com.cn.leedane.model.circle.CircleBean;

/**
 * 圈子mapper接口类
 * @author LeeDane
 * 2017年5月30日 下午8:15:58
 * version 1.0
 */
public interface CircleMapper extends BaseMapper<CircleBean>{
	/**
	 * 获取用户所有的圈子
	 * @param userId
	 * @return
	 */
	public List<CircleBean> getAllCircles(@Param("createUserId") int userId, @Param("status") int status);
	
	/**
	 * 判断圈子是否已经存在
	 * @param userId
	 * @return
	 */
	public List<CircleBean> isExists(@Param("name") String name);
	
	/**
	 * 分页获取任务列表
	 * @param start
	 * @param pageSize
	 * @return
	 */
	public List<Map<String, Object>> paging(
			@Param("createUserId")int createUserId,
			@Param("roleType")int roleType,
			@Param("start")int start, 
			@Param("pageSize")int pageSize, 
			@Param("status") int status);
	
}
