package com.cn.leedane.controller;

import com.cn.leedane.model.NotificationBean;
import com.cn.leedane.service.NotificationService;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.JsonUtil;
import com.cn.leedane.utils.ResponseMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping(value = ControllerBaseNameUtil.nf)
public class NotificationController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());
	//通知service
	@Autowired
	private NotificationService<NotificationBean> notificationService;
	
	/**
	 * 发送广播
	 * @return
	 */
	@RequestMapping(value = "/broadcast", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> sendBroadcast(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(notificationService.sendBroadcast(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 获取通知列表
	 * @return
	 */
	@RequestMapping(value = "/notifications", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> limit(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(notificationService.getLimit(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 分页通知列表
	 * @return
	 */
	@RequestMapping(value = "/notifications/paging", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> paging(
			@RequestParam("type") String type, 
			@RequestParam(value = "page_size", required = false) Integer pageSize, 
			@RequestParam("current") Integer current, 
			@RequestParam("total") Integer total,
			Model model, 
			HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(notificationService.paging(type, pageSize, current, total, getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 删除通知
	 * @return
	 */
	@RequestMapping(value = "/notification", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> delete(@RequestParam(value = "nid", required = false) long nid, Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		if(nid < 1)
			nid = JsonUtil.getLongValue(getJsonFromMessage(message), "nid");
		message.putAll(notificationService.deleteNotification(nid, getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 更新通知为已读状态
	 * @return
	 */
	@RequestMapping(value = "/notification", method = RequestMethod.PUT, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> updateRead(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(notificationService.updateRead(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 更新通知为已读状态
	 * @return
	 */
	@RequestMapping(value = "/notification/all", method = RequestMethod.PUT, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> updateAllRead(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(notificationService.updateAllRead(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 获取登录用户未读取消息的数量
	 * @return
	 */
	@RequestMapping(value = "/notification/noread/number", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> noReadNumber(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(notificationService.noReadNumber(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
}
