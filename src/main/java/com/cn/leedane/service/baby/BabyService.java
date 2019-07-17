package com.cn.leedane.service.baby;

import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;
import net.sf.json.JSONObject;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 宝宝的Service类
 * @author LeeDane
 * 2018年6月6日 下午5:05:08
 * version 1.0
 */
@Transactional
public interface BabyService <T extends IDBean>{
	/**
	 * 添加新宝宝
	 * @param jo 参数
	 * @param user 用户
	 * @param request
	 * @return
	 */
	public Map<String,Object> add(JSONObject jo, UserBean user, HttpRequestInfoBean request);
	
	/**
	 * 编辑宝宝
	 * @param babyId
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String,Object> update(int babyId, JSONObject jo, UserBean user, HttpRequestInfoBean request);
	
	/**
	 * 删除宝宝
	 * @param babyId 参数
	 * @param user 用户
	 * @param request
	 * @return
	 */
	public Map<String,Object> delete(int babyId, UserBean user, HttpRequestInfoBean request);

	/**
	 * 将宝宝的状态改变成出生状态
	 * @param babyId
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> changeBorn(int babyId,
			JSONObject json, UserBean user,
			HttpRequestInfoBean request);
}	
