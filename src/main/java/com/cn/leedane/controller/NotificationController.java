package com.cn.leedane.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cn.leedane.model.NotificationBean;
import com.cn.leedane.service.NotificationService;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.ResponseMap;

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
	public Map<String, Object> sendBroadcast(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		try {
			if(!checkParams(message, request))
				return message.getMap();
			
			message.putAll(notificationService.sendBroadcast(getJsonFromMessage(message), getUserFromMessage(message), request));
			return message.getMap();
		} catch (Exception e) {
			e.printStackTrace();
		}
		message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		return message.getMap();
	}
	
	/**
	 * 添加通知列表
	 * @return
	 */
	@RequestMapping(value = "/notifications", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> paging(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		try {
			if(!checkParams(message, request))
				return message.getMap();
			
			message.putAll(notificationService.getLimit(getJsonFromMessage(message), getUserFromMessage(message), request));
			return message.getMap();
		} catch (Exception e) {
			e.printStackTrace();
		}
		message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		return message.getMap();
	}
	
	/**
	 * 删除通知
	 * @return
	 */
	@RequestMapping(value = "/notification", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> delete(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		try {
			if(!checkParams(message, request))
				return message.getMap();
			
			message.putAll(notificationService.deleteNotification(getJsonFromMessage(message), getUserFromMessage(message), request));
			return message.getMap();
		} catch (Exception e) {
			e.printStackTrace();
		}
		message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		return message.getMap();
	}
	
	/**
	 * 发送私信
	 * @return
	 */
	@RequestMapping(value = "/privatechat", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> sendPrivateChat(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		try {
			if(!checkParams(message, request))
				return message.getMap();
			
			message.putAll(notificationService.deleteNotification(getJsonFromMessage(message), getUserFromMessage(message), request));
			return message.getMap();
		} catch (Exception e) {
			e.printStackTrace();
		}
		message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		return message.getMap();
	}
}
