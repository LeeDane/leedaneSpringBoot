package com.cn.leedane.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.cn.leedane.model.SignInBean;

/**
 * 签到mapper接口类
 * @author LeeDane
 * 2016年7月12日 上午11:17:46
 * Version 1.0
 */
public interface SignInMapper extends BaseMapper<SignInBean>{
	/**
	 * 用户指定时间是否已经签到
	 * @param userId 用户ID
	 * @param dateTime 指定的日期
	 * @return
	 */
	public List<Map<String, Object>> isSign(@Param("userId")int userId, @Param("dateTime")String dateTime);
	
	/**
	 * 用户历史上是否有签到记录
	 * @param userId 用户ID
	 * @return
	 */
	public List<Map<String, Object>> hasHistorySign(@Param("userId")int userId);
	
	/**
	 * 获取数据库中最新的记录
	 * @param userId
	 * @return
	 */
	public List<Map<String, Object>> getNewestRecore(@Param("userId") int userId);
	
	/**
	 * 获取昨天的签到记录
	 * @param uid
	 * @return
	 */
	public List<Map<String, Object>> getYesTodayRecore(@Param("userId") int userId, @Param("createTime") Date createTime, @Param("status") int status);
	
	/**
	 * 获取用户当前的积分
	 * @param uid
	 * @return
	 */
	//public int getScore(int uid);
}
