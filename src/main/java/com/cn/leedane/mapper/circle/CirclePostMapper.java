package com.cn.leedane.mapper.circle;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.cn.leedane.mapper.BaseMapper;
import com.cn.leedane.model.circle.CirclePostBean;

/**
 * 帖子mapper接口类
 * @author LeeDane
 * 2017年6月20日 下午5:57:54
 * version 1.0
 */
public interface CirclePostMapper extends BaseMapper<CirclePostBean>{

	/**
	 * 获取帖子对象
	 * @param postId
	 * @param status
	 * @return
	 */
	public List<Map<String, Object>> getCirclePost(@Param("postId")int postId, @Param("status")int status);
	
	/**
	 * 分页获取帖子列表
	 * @param start
	 * @param pageSize
	 * @return
	 */
	public List<Map<String, Object>> paging(
			@Param("circleId") long circleId,
			@Param("start") int start,
			@Param("pageSize") int pageSize, 
			@Param("status") int status);

	/**
	 * 在圈子中心获取用户的帖子列表
	 * @param userId
	 * @param start
	 * @param pageSize
	 * @param status
	 * @return
	 */
	public List<CirclePostBean> getUserCirclePosts(
			@Param("userId") long userId,
			@Param("start") int start,
			@Param("pageSize") int pageSize, 
			@Param("status") int status);

	/**
	 * 在帖子中心获取用户的帖子列表
	 * @param circleId
	 * @param userId
	 * @param start
	 * @param pageSize
	 * @param statusl
	 * @return
	 */
	public List<CirclePostBean> getUserPostPosts(
			@Param("circleId") long circleId,
			@Param("userId") long userId,
			@Param("start") int start,
			@Param("pageSize") int pageSize, 
			@Param("status") int statusl);
	
	/**
	 * 获取目前最热门的帖子(计算)
	 * @return
	 */
	public void calculateHotests(@Param("time") Date time, @Param("pageSize") int pageSize);
	
	/**
	 * 在圈子中心获取用户的帖子列表
	 * @param start
	 * @param pageSize
	 * @param status
	 * @return
	 */
	public List<CirclePostBean> getHostestPosts(
			@Param("time") Date time, 
			@Param("start") int start,
			@Param("pageSize") int pageSize, 
			@Param("status") int status);
}
