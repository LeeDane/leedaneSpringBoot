package com.cn.leedane.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import net.sf.json.JSONObject;

import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;

/**
 * 聊天背景相关service接口类
 * @author LeeDane
 * 2016年7月12日 上午11:31:09
 * Version 1.0
 */
@Transactional("txManager")
public interface ChatBgService <T extends IDBean>{
	
	
	/**
	 * 获取聊天背景的分页列表
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	//标记该方法不需要事务
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> paging(JSONObject jo, UserBean user, HttpServletRequest request);

	/**
	 * 发布聊天背景
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> publish(JSONObject jo, UserBean user, HttpServletRequest request);

	/**
	 * 下载付费版本的聊天背景
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> addChatBg(JSONObject jo, UserBean user, HttpServletRequest request);
}
