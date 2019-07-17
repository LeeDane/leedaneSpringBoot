package com.cn.leedane.service.clock;

import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;
import net.sf.json.JSONObject;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 任务打卡的Service类
 * @author LeeDane
 * 2018年9月11日 下午9:53:26
 * version 1.0
 */
@Transactional
public interface ClockInService <T extends IDBean>{
	/**
	 * 添加任务打卡
	 * @param jo 参数
	 * @param user 用户
	 * @param request
	 * @return
	 */
	public  Map<String,Object> add(JSONObject jo, UserBean user, HttpRequestInfoBean request);
	
	/**
	 * 编辑任务打卡
	 * @param clockInId
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public  Map<String,Object> update(int clockInId, JSONObject jo, UserBean user, HttpRequestInfoBean request);
	
	/**
	 * 删除任务打卡
	 * @param clockInId 参数
	 * @param user 用户
	 * @param request
	 * @return
	 */
	public  Map<String,Object> delete(int clockInId, UserBean user, HttpRequestInfoBean request);


	/**
	 * 	获取任务在制定日期的打卡情况
	 * @param clockId
	 * @param date
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
    public Map<String, Object> clockIns(int clockId, String date, JSONObject json, UserBean user, HttpRequestInfoBean request);

	/**
	 * 获取用户的打卡详情
	 * @param clockId
	 * @param toUserId
	 * @param date
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
    public Map<String, Object> getUserClockIn(int clockId, int toUserId, String date, JSONObject json, UserBean user, HttpRequestInfoBean request);

	/**
	 * 提醒继续发消息
	 * @param clockId
	 * @param clockInId
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
    public Map<String, Object> userClockInNotification(int clockId, int clockInId, JSONObject json, UserBean user, HttpRequestInfoBean request);

	/**
	 * 审核
	 * @param clockId
	 * @param clockInId
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> clockInCheck(int clockId, int clockInId, JSONObject json, UserBean user, HttpRequestInfoBean request);

	/**
	 * 添加位置信息
	 * @param clockId
	 * @param clockInId
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
    public Map<String, Object> clockInAddLocation(int clockId, int clockInId, JSONObject json, UserBean user, HttpRequestInfoBean request);

	/**
	 * 添加图片信息
	 * @param clockId
	 * @param clockInId
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> clockInAddImage(int clockId, int clockInId, JSONObject json, UserBean user, HttpRequestInfoBean request);

	/**
	 * 删除打卡资源(管理员和自己才能操作)
	 * @param clockId
	 * @param clockInId
	 * @param resourceId
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> deleteResource(int clockId, int clockInId, int resourceId, JSONObject json, UserBean user, HttpRequestInfoBean request);
}
