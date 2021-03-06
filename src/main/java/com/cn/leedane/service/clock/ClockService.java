package com.cn.leedane.service.clock;

import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;
import net.sf.json.JSONObject;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

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
	public  Map<String,Object> add(JSONObject jo, UserBean user, HttpRequestInfoBean request);
	
	/**
	 * 编辑任务提醒
	 * @param babyId
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public  Map<String,Object> update(long babyId, JSONObject jo, UserBean user, HttpRequestInfoBean request);
	
	/**
	 * 删除任务提醒
	 * @param clockId 参数
	 * @param user 用户
	 * @param request
	 * @return
	 */
	public  Map<String,Object> delete(long clockId, UserBean user, HttpRequestInfoBean request);
	
	/**
	 * 获取指定日期的打卡任务提醒
	 * @param date
	 * @param user
	 * @param request
	 * @return
	 */
	public  Map<String,Object> dateClocks(String date, UserBean user, HttpRequestInfoBean request);
	
	/**
	 * 获取进行中的任务提醒列表
	 * @param user
	 * @param jo
	 * @param request
	 * @return
	 */
	public  Map<String,Object> getOngoingClocks(UserBean user, JSONObject jo, HttpRequestInfoBean request);
	
	/**
	 * 获取结束的任务提醒列表
	 * @param user
	 * @param jo
	 * @param request
	 * @return
	 */
	public  Map<String,Object> getEndeds(UserBean user, JSONObject jo, HttpRequestInfoBean request);
	
	/**
	 * 获取系统的默认任务
	 * @param date
	 * @param user
	 * @param request
	 * @return
	 */
	public  Map<String,Object> systemClocks(UserBean user, HttpRequestInfoBean request);

	/**
	 * 获取任务的信息
	 * @param clockId
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> getClock(
			long clockId, JSONObject json, UserBean user,
			HttpRequestInfoBean request);
			
	/**
	 * 获取任务的缩略信息
	 * @param clockId
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> getClockThumbnail(
			long clockId, JSONObject json, UserBean user,
			HttpRequestInfoBean request);		

	/**
	 * 搜索任务(只支持搜索共享的任务或者是自己的任务，返回最多10条记录)
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> search(
			JSONObject json, UserBean user,
			HttpRequestInfoBean request);

	/**
	 * 获取任务的统计信息
	 * @param clockId
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> statistics(long clockId,
			JSONObject json, UserBean user,
			HttpRequestInfoBean request);

	/**
	 * 获取任务的资源列表
	 * @param clockId
	 * @param resourceType
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> resources(long clockId, int resourceType, JSONObject json, UserBean user, HttpRequestInfoBean request);
}
