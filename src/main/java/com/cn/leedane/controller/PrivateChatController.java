package com.cn.leedane.controller;

import com.cn.leedane.model.PrivateChatBean;
import com.cn.leedane.service.PrivateChatService;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.ResponseMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping(value = ControllerBaseNameUtil.pc)
public class PrivateChatController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());
	@Autowired
	private PrivateChatService<PrivateChatBean> privateChatService;
	
	/**
	 * 发送私信
	 * @return
	 */
	@RequestMapping(value = "/privatechat", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> send(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(privateChatService.send(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 分页获取聊天历史列表(两个人的聊天)
	 * @return
	 */
	@RequestMapping(value = "/privatechats", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> paging(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(privateChatService.getLimit(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
}
