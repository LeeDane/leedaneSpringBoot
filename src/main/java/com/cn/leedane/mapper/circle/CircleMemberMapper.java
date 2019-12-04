package com.cn.leedane.mapper.circle;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.cn.leedane.mapper.BaseMapper;
import com.cn.leedane.model.circle.CircleMemberBean;

/**
 * 圈子成员mapper接口类
 * @author LeeDane
 * 2017年5月30日 下午8:15:58
 * version 1.0
 */
public interface CircleMemberMapper extends BaseMapper<CircleMemberBean>{

	/**
	 * 判断成员是否已经在圈子中
	 * @param memberId
	 * @param circleId
	 * @return
	 */
	public List<CircleMemberBean> getMember(@Param("memberId") long memberId, @Param("circleId") long circleId, @Param("status") int status);

	/**
	 * 获取圈子的管理员列表
	 * @param circleId
	 * @param roleType
	 * @param status
	 * @return
	 */
	public List<Map<String, Object>> getMembersByRoleType(
			@Param("circleId") long circleId
			, @Param("roleType") int roleType, 
			@Param("status") int status);

	/**
	 * 获取圈子的所有成员
	 * @param circleId
	 * @param status
	 * @return
	 */
	public List<Map<String, Object>> getAllMembers(
			@Param("circleId") long circleId,
			@Param("status") int status);

	/**
	 * 根据权限ids获取其对应分配的用户id
	 * @param memberIds
	 * @param circleId
	 * @param status
	 * @return
	 */
	public List<CircleMemberBean> findByMemberIds(
			@Param("memberIds")int[] memberIds,
			@Param("circleId") long circleId,
			@Param("status") int status);
	
	/**
	 * 批量修改
	 * @param data
	 * @param roleType
	 * @param circleId
	 */
	public void updateByBatch(@Param("list")List<Map<String, Object>> data, 
			@Param("roleType") int roleType,
			@Param("circleId") long circleId);

	/**
	 * 分页获取圈子成员列表
	 * @param circleId
	 * @param start
	 * @param pageSize
	 * @param status
	 * @return
	 */
	public List<Map<String, Object>> paging(
			@Param("circleId")long circleId,
			@Param("start")int start, 
			@Param("pageSize")int pageSize, 
			@Param("status") int status);

	/**
	 * 获取目前最热门的圈子成员(只计算)
	 * @param circleId
	 */
	public void calculateHotests(@Param("circleId") long circleId);
	
	/**
	 * 获取目前圈子最热门的成员
	 * @param circleId
	 * @param time
	 * @param pageSize
	 * @return
	 */
	public List<Map<String, Object>> getHotests(@Param("circleId")long circleId, @Param("time") Date time, @Param("pageSize") int pageSize, @Param("status") int status);
	
	/**
	 * 获取目前圈子最新的成员
	 * @param circleId
	 * @param pageSize
	 * @return
	 */
	public List<Map<String, Object>> getNewests(@Param("circleId")long circleId, @Param("pageSize") int pageSize, @Param("status") int status);
	
	/**
	 * 获取目前圈子推荐的成员
	 * @param circleId
	 * @param pageSize
	 * @return
	 */
	public List<Map<String, Object>> getRecommends(@Param("circleId")long circleId, @Param("pageSize") int pageSize, @Param("status") int status);
}
