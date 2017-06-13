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

}
