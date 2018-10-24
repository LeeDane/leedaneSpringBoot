package com.cn.leedane.service.clock;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.transaction.annotation.Transactional;

import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;

/**
 * 任务提醒的Service类
 * @author LeeDane
 * 2018年8月29日 下午5:33:29
 * version 1.0
 */
@Transactional
public interface ClockService <T extends IDBean>{
	/**
	 * 添加任务提醒
	 * @param jo 参数
	 * @param user 用户
	 * @param request
	 * @return
	 */
	public  Map<String,Object> add(JSONObject jo, UserBean user, HttpServletRequest request);
	
	/**
	 * 编辑任务提醒
	 * @param babyId
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public  Map<String,Object> update(int babyId, JSONObject jo, UserBean user, HttpServletRequest request);
	
	/**
	 * 删除任务提醒
	 * @param clockId 参数
	 * @param user 用户
	 * @param request
	 * @return
	 */
	public  Map<String,Object> delete(int clockId, UserBean user, HttpServletRequest request);
	
	/**
	 * 获取指定日期的打卡任务提醒
	 * @param date
	 * @param user
	 * @param request
	 * @return
	 */
	public  Map<String,Object> dateClocks(String date, UserBean user, HttpServletRequest request);
	
	/**
	 * 获取进行中的任务提醒列表
	 * @param user
	 * @param jo
	 * @param request
	 * @return
	 */
	public  Map<String,Object> getOngoingClocks(UserBean user, JSONObject jo, HttpServletRequest request);
	
	/**
	 * 获取结束的任务提醒列表
	 * @param user
	 * @param jo
	 * @param request
	 * @return
	 */
	public  Map<String,Object> getEndeds(UserBean user, JSONObject jo, HttpServletRequest request);
	
	/**
	 * 获取系统的默认任务
	 * @param date
	 * @param user
	 * @param request
	 * @return
	 */
	public  Map<String,Object> systemClocks(UserBean user, HttpServletRequest request);

	/**
	 * 获取任务的信息
	 * @param clockId
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> getClock(
			int clockId, JSONObject json, UserBean user,
			HttpServletRequest request);
			
	/**
	 * 获取任务的缩略信息
	 * @param clockId
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> getClockThumbnail(
			int clockId, JSONObject json, UserBean user,
			HttpServletRequest request);		

	/**
	 * 搜索任务(只支持搜索共享的任务或者是自己的任务，返回最多10条记录)
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> search(
			JSONObject json, UserBean user,
			HttpServletRequest request);
}	
