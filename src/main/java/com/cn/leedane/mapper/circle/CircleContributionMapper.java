package com.cn.leedane.mapper.circle;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.cn.leedane.mapper.BaseMapper;
import com.cn.leedane.model.circle.CircleContributionBean;

/**
 * 贡献值的mapper接口类
 * @author LeeDane
 * 2017年6月14日 下午5:29:43
 * version 1.0
 */
public interface CircleContributionMapper extends BaseMapper<CircleContributionBean>{

	/**
	 * 获取今天之前的总贡献值
	 * @param circleId
	 * @param userId
	 * @return
	 */
	public List<Map<String, Object>> getTotalScore(@Param("circleId")int circleId, @Param("createUserId")int userId);
	
	/**
	 * 获取贡献值
	 * @param user
	 * @param id
	 * @param date
	 * @return
	 */
	public List<Map<String, Object>> getContribute(
			@Param("circleId") int circleId, 
			@Param("userId")int userId);

}
