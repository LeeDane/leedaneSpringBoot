package com.cn.leedane.mapper.circle;

import java.util.Date;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.ibatis.annotations.Param;

import com.cn.leedane.mapper.BaseMapper;
import com.cn.leedane.model.UserBean;
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
			@Param("circleId") int circleId, 
			@Param("createUserId")int userId, 
			@Param("status")int status,
			@Param("time")String time);
}
