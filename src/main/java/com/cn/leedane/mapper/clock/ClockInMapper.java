package com.cn.leedane.mapper.clock;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.cn.leedane.mapper.BaseMapper;
import com.cn.leedane.model.UsersBean;
import com.cn.leedane.model.clock.ClockInBean;

/**
 * 任务打卡mapper接口类
 * @author LeeDane
 * 2018年9月11日 下午9:55:13
 * version 1.0
 */
public interface ClockInMapper extends BaseMapper<ClockInBean>{
	/**
	 * 获得指定日期范围内指定任务的打卡情况
	 * @param clockId
	 * @param startDate
	 * @param end
	 * @return
	 */
	public List<Map<String, Object>> getClockInsRangeDate(
			@Param("clockId")int clockId, 
			@Param("start")String startDate, 
			@Param("end")String end);
			
	/**
	 * 获得任务Top3排行的成员
	 * @param clockId
	 * @return
	 */
	public List<Map<String, Object>> getTopMember(@Param("clockId")int clockId);
	
	/**
	 * 模拟插入成员
	 * @param users
	 * @return
	 */
	public int addUser(@Param("users")UsersBean users);
}
