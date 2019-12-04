package com.cn.leedane.service;

import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;
import net.sf.json.JSONObject;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 大事件service接口类
 * @author LeeDane
 * 2019年7月19日 下午18:33:50
 * Version 1.0
 */
@Transactional
public interface EventService<T extends IDBean>{
	/**
	 * 添加大事件
	 * @param jsonObject
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> add(JSONObject jsonObject, UserBean user, HttpRequestInfoBean request);

	/**
	 * 修改大事件
	 * @param jsonObject
	 * @param request
	 * @param user
	 * @return
	 */
	public Map<String, Object> update(JSONObject jsonObject, HttpRequestInfoBean request, UserBean user);

	/**
	 * 删除大事件
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> delete(JSONObject jo, UserBean user, HttpRequestInfoBean request);

	/**
	 * 获取大事件列表
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> events(JSONObject jo, UserBean user, HttpRequestInfoBean request);

	/**
	 * 获取所有的大事件列表
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> all(JSONObject jo, UserBean user, HttpRequestInfoBean request);
}
