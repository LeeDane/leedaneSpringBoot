package com.cn.leedane.service.clock;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.transaction.annotation.Transactional;

import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;

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
	public  Map<String,Object> add(JSONObject jo, UserBean user, HttpServletRequest request);
	
	/**
	 * 编辑任务打卡
	 * @param clockInId
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public  Map<String,Object> update(int clockInId, JSONObject jo, UserBean user, HttpServletRequest request);
	
	/**
	 * 删除任务打卡
	 * @param clockInId 参数
	 * @param user 用户
	 * @param request
	 * @return
	 */
	public  Map<String,Object> delete(int clockInId, UserBean user, HttpServletRequest request);


	/**
	 * 	获取任务在制定日期的打卡情况
	 * @param clockId
	 * @param date
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
    public Map<String, Object> clockIns(int clockId, String date, JSONObject json, UserBean user, HttpServletRequest request);
}
