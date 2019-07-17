package com.cn.leedane.service;

import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;
import net.sf.json.JSONObject;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 粉丝service接口类
 * @author LeeDane
 * 2016年7月12日 上午11:33:00
 * Version 1.0
 */
@Transactional
public interface FanService <T extends IDBean>{
	
	/**
	 * 关注成为其粉丝
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> addFan(JSONObject jo, UserBean user, HttpRequestInfoBean request);
	
	/**
	 * 获取我的全部的关注的用户的ID和备注
	 * 注意：为了业务分开，这里只能调用的是登录用户的关注的用户列表，非登录用户的关注列表请调用getToAttentionsLimit()
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> getMyAttentionsLimit(JSONObject jo, UserBean user, HttpRequestInfoBean request);
	
	
	/**
	 * 获取Ta关注的用户的ID和备注
	 * 注意：为了业务分开，这里只能调用的是非登录用户的关注的用户列表，登录用户的关注列表请调用getToAttentionsLimit()
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> getToAttentionsLimit(JSONObject jo, UserBean user, HttpRequestInfoBean request);
	
	/**
	 * 获取全部的关注我的ID和备注
	 * 注意：为了业务分开，这里只能调用的是登录用户的粉丝列表，非登录用户的粉丝列表请调用getToFansLimit()
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> getMyFansLimit(JSONObject jo, UserBean user, HttpRequestInfoBean request);
	
	/**
	 * 获取全部的关注Ta的ID(是否关注的展示相对我)
	 * 注意：为了业务分开，这里只能调用的是非登录用户的粉丝列表，登录用户的粉丝列表请调用getMyFansLimit()
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> getToFansLimit(JSONObject jo, UserBean user, HttpRequestInfoBean request);
	
	/**
	 * 取消关注粉丝
	 * @param uid
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public boolean cancel(JSONObject jo, UserBean user, HttpRequestInfoBean request);
	
	/**
	 * 判断两人是否互粉
	 * @param id  当前用户的id
	 * @param to_user_id  对方用户的id
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public boolean isFanEachOther(JSONObject jo, UserBean user, HttpRequestInfoBean request);
	/**
	 * 是否粉她
	 * @param id  当前用户的id
	 * @param to_user_id  对方用户的id
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> isFan(JSONObject jo, UserBean user, HttpRequestInfoBean request);
	
	/**
	 * 执行SQL对应字段的List<Map<String,Object>
	 * @param sql sql语句,参数直接写在语句中，存在SQL注入攻击de风险，慎用
	 * @param params ?对应的值
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<Map<String, Object>> executeSQL(String sql, Object ...params);
	
	
	/**
	 * 获取标注名称
	 * @param userId
	 * @param toUserId
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public String getRemark(int userId, int toUserId, int status);
}
