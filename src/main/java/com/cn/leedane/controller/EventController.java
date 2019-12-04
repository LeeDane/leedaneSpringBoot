package com.cn.leedane.controller;

import com.cn.leedane.model.EventBean;
import com.cn.leedane.service.EventService;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.ResponseMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 大事件
 */
@RestController
@RequestMapping(value = ControllerBaseNameUtil.ev)
public class EventController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());

	//大事件service
	@Autowired
	private EventService<EventBean> eventService;

	/**
	 * 修改大事件
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value= "/manage", method = RequestMethod.PUT, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> send(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(eventService.update(getJsonFromMessage(message), getHttpRequestInfo(request), getUserFromMessage(message)));
		return message.getMap();
	}

	/**
	 * 添加大事件
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value= "/manage", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> sendDraft(Model model, HttpServletRequest request) throws Exception{
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(eventService.add(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}


	/**
	 * 删除大事件
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value= "/manage", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> delete(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(eventService.delete(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 获取指定符合条件的大事件列表
	 * @return
	 */
	@RequestMapping(value = "/events", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> events(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(eventService.events(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}

	/**
	 * 获取所有的大事件列表
	 * @return
	 */
	@RequestMapping(value = "/all", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> all(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		message.putAll(eventService.all(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
}
