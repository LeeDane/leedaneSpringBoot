package com.cn.leedane.service;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 聊天广场的Service类
 * @author LeeDane
 * 2017年2月10日 下午4:10:18
 * Version 1.0
 */
@Transactional("txManager")
public interface ChatSquareService<ChatSquareBean>{

	/**
	 * 添加数据
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> addChatSquare(int userId, String message) ;
	
	/**
	 * 获取指定时间的活跃的用户
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	//标记该方法不需要事务
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> getActiveUser(Date date, int top) ;


	/**
	 * 获取聊天列表
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	//标记该方法不需要事务
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> getLimit(JSONObject jo, HttpServletRequest request);
}
