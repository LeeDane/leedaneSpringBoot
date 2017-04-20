package com.cn.leedane.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cn.leedane.model.ChatBean;
import com.cn.leedane.service.ChatService;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.ResponseMap;

@RestController
@RequestMapping(value = ControllerBaseNameUtil.ct)
public class ChatController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());

	@Autowired
	private ChatService<ChatBean> chatService;
	
	/**
	 * 发送聊天信息
	 * @return
	 */
	@RequestMapping(value = "/chat", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> send(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(request);
		message.putAll(chatService.send(getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
	
	/**
	 * 删除聊天记录
	 * @return
	 */
	@RequestMapping(value = "/chat", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> delete(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(request);
		message.putAll(chatService.deleteChat(getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
	
	/**
	 * 分页获取聊天历史列表(两个人的聊天)
	 * @return
	 */
	@RequestMapping(value = "/chats", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> paging(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(request);
		message.putAll(chatService.getLimit(getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
	
	/**
	 * 更新聊天信息为已读状态
	 * @return
	 */
	@RequestMapping(value = "/chat", method = RequestMethod.PUT, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> updateRead(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(request);
		message.putAll(chatService.updateRead(getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
	
	/**
	 * 获取用户全部未读的信息
	 * @return
	 */
	@RequestMapping(value = "/noReads", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> noReadList(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(request);
		message.putAll(chatService.noReadList(getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
	
	/**
	 * 获取登录用户的全部与其有过聊天记录的用户的最新一条聊天信息
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/chat", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> getOneChatByAllUser(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(request);
		message.putAll(chatService.getOneChatByAllUser(getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
}
