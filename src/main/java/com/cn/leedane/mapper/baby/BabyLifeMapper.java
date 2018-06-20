package com.cn.leedane.mapper.baby;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cn.leedane.mapper.BaseMapper;
import com.cn.leedane.model.baby.BabyLifeBean;

/**
 * 宝宝生活状态mapper接口类
 * @author LeeDane
 * 2018年6月11日 下午6:34:46
 * version 1.0
 */
public interface BabyLifeMapper extends BaseMapper<BabyLifeBean>{
	/**
	 * 获取用户所有的宝宝
	 * @param userId
	 * @return
	 */
	public List<BabyLifeBean> getBabyLifes(@Param("createUserId") int createUserId, @Param("babyId") int babyId, @Param("status") int status, 
			@Param("start")int start, @Param("pageSize") int pageSize);

	/**
	 * 获取宝宝的生活方式(没有分页，一次性最多获取pageSize个数据)
	 * @param babyId
	 * @param startDate
	 * @param endDate
	 * @param start
	 * @param pageSize
	 * @param statusNormal
	 * @return
	 */
	public List<BabyLifeBean> lifes(@Param("createUserId") int createUserId, 
			@Param("babyId") int babyId, 
			@Param("startDate") String startDate,
			@Param("endDate") String endDate, 
			@Param("keyWord") String keyword, 
			@Param("lifeType") int lifeType, 
			@Param("start") int start, 
			@Param("pageSize") int pageSize, 
			@Param("status") int status);
}
