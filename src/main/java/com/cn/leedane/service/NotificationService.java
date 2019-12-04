package com.cn.leedane.service;

import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.NotificationBean;
import com.cn.leedane.model.UserBean;
import net.sf.json.JSONObject;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
/**
 * 通知Service类
 * @author LeeDane
 * 2016年7月12日 上午11:34:00
 * Version 1.0
 */
@Transactional
public interface NotificationService<T extends IDBean>{
	
	/**
	 * 基础的保存实体的方法
	 * @param t
	 * @return
	 */
	public boolean save(NotificationBean t);
	
	/**
	 * 基础更新实体的方法
	 * @param t
	 * @return
	 */
	public boolean update(NotificationBean t);
	
	/**
	 * 获取通知列表
	 * {'table_name':'t_mood','table_id':1, 'method':'firstloadings'
	 * 'pageSize':5,'last_id':0,'first_id':0}
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> getLimit(JSONObject jo, UserBean user,
			HttpRequestInfoBean request);
	/**
	 * 分页获取通知列表
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> paging(String type,
			int pageSize, int current, int total,
			UserBean userFromMessage, HttpRequestInfoBean request);
	
	/**
	 * 发送广播
	 * {'broadcast':'大家好'}
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> sendBroadcast(JSONObject jo, UserBean user,
			HttpRequestInfoBean request);
	
	/**
	 * 删除通知
	 * @param nid
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> deleteNotification(long nid, UserBean user, HttpRequestInfoBean request);
	
	/**
	 * 更新通知为已读状态
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> updateRead(JSONObject jo, UserBean user, HttpRequestInfoBean request);
	/**
	 * 全部更新通知为已读状态
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> updateAllRead(JSONObject jo, UserBean user, HttpRequestInfoBean request);
	
	/**
	 * 获取登录用户未读取消息的数量
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> noReadNumber(JSONObject jo, UserBean user, HttpRequestInfoBean request);
	
}
