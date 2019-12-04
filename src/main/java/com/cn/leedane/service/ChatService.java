package com.cn.leedane.service;

import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;
import net.sf.json.JSONObject;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 聊天相关service接口类
 * @author LeeDane
 * 2016年7月12日 上午11:31:34
 * Version 1.0
 */
@Transactional
public interface ChatService <T extends IDBean>{
	
	
	/**
	 * 获取聊天信息的分页列表
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> getLimit(JSONObject jo, UserBean user, HttpRequestInfoBean request);

	/**
	 * 发送聊天信息
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> send(JSONObject jo, UserBean user, HttpRequestInfoBean request);

	/**
	 * 更新聊天信息为已读状态
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> updateRead(JSONObject jo, UserBean user, HttpRequestInfoBean request);

	/**
	 * 获取用户全部未读的信息
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> noReadList(JSONObject jo, UserBean user, HttpRequestInfoBean request);

	/**
	 * 删除聊天记录
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> deleteChat(JSONObject jo, UserBean user, HttpRequestInfoBean request);

	/**
	 * 获取登录用户的全部与其有过聊天记录的用户的最新一条聊天信息
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> getOneChatByAllUser(JSONObject json, UserBean user, HttpRequestInfoBean request);
}
