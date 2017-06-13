package com.cn.leedane.mapper.circle;

import org.apache.ibatis.annotations.Param;

import com.cn.leedane.mapper.BaseMapper;
import com.cn.leedane.model.circle.CircleCreateLimitBean;

/**
 * 圈子创建控制mapper接口类
 * @author LeeDane
 * 2017年6月11日 下午4:33:31
 * version 1.0
 */
public interface CircleCreateLimitMapper extends BaseMapper<CircleCreateLimitBean>{
	/**
	 * 获取能创建的圈子数量
	 * @param userId
	 * @return
	 */
	public int getNumber(@Param("createUserId") int createUserId, @Param("status") int status);
}
