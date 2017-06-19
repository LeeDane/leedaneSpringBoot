package com.cn.leedane.mapper.circle;

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
	public List<CircleMemberBean> getMember(@Param("memberId") int memberId, @Param("circleId") int circleId, @Param("status") int status);
	
	/**
	 * 获取圈子的管理员列表
	 * @param memberId
	 * @param circleId
	 * @return
	 */
	public List<Map<String, Object>> getMembersByRoleType(
			@Param("circleId") int circleId
			, @Param("roleType") int roleType, 
			@Param("status") int status);

	/**
	 * 获取圈子的所有成员
	 * @param memberId
	 * @param circleId
	 * @return
	 */
	public List<Map<String, Object>> getAllMembers(
			@Param("circleId") int circleId, 
			@Param("status") int status);
	
	/**
	 * 根据权限ids获取其对应分配的用户id
	 * @param pmids
	 * @return
	 */
	public List<CircleMemberBean> findByMemberIds(
			@Param("memberIds")int[] memberIds,
			@Param("circleId") int circleId, 
			@Param("status") int status);
	
	/**
	 * 批量修改
	 * @param data
	 * @param roleType
	 * @param circleId
	 */
	public void updateByBatch(@Param("list")List<Map<String, Object>> data, 
			@Param("roleType") int roleType,
			@Param("circleId") int circleId);
	
	
}
