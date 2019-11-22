package com.cn.leedane.mapper.circle;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cn.leedane.mapper.BaseMapper;
import com.cn.leedane.model.circle.CircleClockInBean;

/**
 * 圈子打卡mapper接口类
 * @author LeeDane
 * 2017年6月14日 下午4:31:33
 * version 1.0
 */
public interface CircleClockInMapper extends BaseMapper<CircleClockInBean>{

	/**
	 * 根据条件获取bean
	 * @return
	 */
	public List<CircleClockInBean> getClockInBean(
			@Param("circleId") long circleId,
			@Param("createUserId")long userId,
			@Param("status")int status,
			@Param("time")String time);
}
