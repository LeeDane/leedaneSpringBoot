package com.cn.leedane.controller;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cn.leedane.model.ChatBgBean;
import com.cn.leedane.service.ChatBgService;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.ResponseMap;

@RestController
@RequestMapping(value = ControllerBaseNameUtil.ctb)
public class ChatBgController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());
	private ChatBgService<ChatBgBean> chatBgService;
	
	@Resource
	public void setChatBgService(ChatBgService<ChatBgBean> chatBgService) {
		this.chatBgService = chatBgService;
	}
	
	/**
	 * 发布聊天背景历史
	 * @return
	 */
	@RequestMapping(value = "/bg", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> publish(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		message.putAll(chatBgService.publish(getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
	
	/**
	 * 分页获取聊天背景列表
	 * @return
	 */
	@RequestMapping(value = "/bgs", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> paging(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		message.putAll(chatBgService.paging(getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
	
	/**
	 * 检验聊天背景
	 * @return
	 */
	@RequestMapping(value = "/bg/verify", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> verifyChatBg(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		message.putAll(chatBgService.addChatBg(getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
}
