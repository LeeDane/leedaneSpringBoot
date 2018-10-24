package com.cn.leedane.mapper.clock;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cn.leedane.display.clock.ClockDisplay;
import com.cn.leedane.display.clock.ClockSearchDisplay;
import com.cn.leedane.mapper.BaseMapper;
import com.cn.leedane.model.clock.ClockBean;

/**
 * 任务提醒mapper接口类
 * @author LeeDane
 * 2018年8月30日 上午10:51:02
 * version 1.0
 */
public interface ClockMapper extends BaseMapper<ClockBean>{
	/**
	 * 获取我指定日期的打卡任务提醒
	 * @param userId
	 * @param week
	 * @return
	 */
	public List<ClockDisplay> dateClocks(
			@Param("createUserId") int userId, 
			@Param("week")String week, 
			@Param("date")String date);
	
	/**
	 * 获取我当前仍然在进行中的任务提醒列表
	 * @param userId
	 * @param start
	 * @param pageSize
	 * @return
	 */
	public List<ClockDisplay> getMyOngoingClocks(
			@Param("createUserId") int userId,
			@Param("start") int start,
			@Param("pageSize") int pageSize,
			@Param("date")String date);
	
	/**
	 * 获取我当前结束的任务提醒列表
	 * @param userId
	 * @param start
	 * @param pageSize
	 * @param status
	 * @return
	 */
	public List<ClockDisplay> getMyEndedClocks(
			@Param("createUserId") int userId,
			@Param("start") int start,
			@Param("pageSize") int pageSize,
			@Param("date")String date);
	
	/**
	 * 分页获取我的全部任务提醒
	 * @param userId
	 * @param start
	 * @param pageSize
	 * @return
	 */
	public List<ClockBean> getMyAllClocks(@Param("createUserId") int userId, @Param("start")int start, @Param("pageSize") int pageSize);

	/**
	 * 获取系统的任务列表
	 * @return
	 */
	public List<ClockDisplay> systemClocks();
	
	/**
	 * 获得指定用户的指定任务
	 * @return
	 */
	public List<ClockDisplay> getMyClock(@Param("createUserId") int userId, @Param("clockId")int clockId);
	
	/**
	 * 获得指定用户的指定的搜索任务
	 * @return
	 */
	public ClockSearchDisplay getClockThumbnail(@Param("userId") int userId, @Param("clockId")int clockId);
	

	/**
	 * 搜索任务(只支持搜索共享的任务或者是自己的任务，返回最多10条记录)
	 * @param userId
	 * @param keyword
	 * @return
	 */
	public List<ClockSearchDisplay> search(@Param("userId")int userId, @Param("keyword")String keyword);
}
