package com.cn.leedane.mapper.circle;

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
	 * @param id
	 * @param toUserId
	 * @param start
	 * @param pageSize
	 * @param statusNormal
	 * @param statusSelf
	 * @return
	 */
	public List<Map<String, Object>> paging(
			@Param("circleId") int circleId, 
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
			@Param("userId") int userId, 
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
			@Param("circleId") int circleId, 
			@Param("userId") int userId, 
			@Param("start") int start,
			@Param("pageSize") int pageSize, 
			@Param("status") int statusl);
}
