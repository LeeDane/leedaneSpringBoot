package com.cn.leedane.service;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import net.sf.json.JSONObject;

import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.NotificationBean;
import com.cn.leedane.model.UserBean;
/**
 * 通知Service类
 * @author LeeDane
 * 2016年7月12日 上午11:34:00
 * Version 1.0
 */
@Transactional("txManager")
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
	//标记该方法不需要事务
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> getLimit(JSONObject jo, UserBean user,
			HttpServletRequest request);
	
	/**
	 * 发送广播
	 * {'broadcast':'大家好'}
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> sendBroadcast(JSONObject jo, UserBean user,
			HttpServletRequest request);
	
	/**
	 * 删除通知
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> deleteNotification(JSONObject jo, UserBean user, HttpServletRequest request);
	
}
