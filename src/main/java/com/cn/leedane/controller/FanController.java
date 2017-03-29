package com.cn.leedane.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cn.leedane.model.FanBean;
import com.cn.leedane.service.FanService;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.ResponseMap;

@RestController
@RequestMapping(value = ControllerBaseNameUtil.fs)
public class FanController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());
	@Autowired
	private FanService<FanBean> fanService;
	
	/**
	 * 取消关注
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/fan", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> cancel(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		try {
			//{"toUserId": 2}
			if(!checkParams(message, request))
				return message.getMap();
			
			message.put("isSuccess", fanService.cancel(getJsonFromMessage(message), getUserFromMessage(message), request));
			return message.getMap();
		} catch (Exception e) {
			e.printStackTrace();
		}
		message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		return message.getMap();
	}
	/**
	 * 添加关注
	 * @return
	 */
	@RequestMapping(value = "/fan", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> add(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		try {
			//{"id":1, "to_user_id": 2}
			if(!checkParams(message, request))
				return message.getMap();
			
			message.putAll(fanService.addFan(getJsonFromMessage(message), getUserFromMessage(message), request));
			return message.getMap();
		} catch (Exception e) {
			e.printStackTrace();
		}
		message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		return message.getMap();
	}
	
	/**
	 * 判读两人是否是好友
	 * @return
	 */
	@RequestMapping(value = "/is", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> isFan(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		try {
			//{"id":1, "to_user_id": 2}
			if(!checkParams(message, request))
				return message.getMap();
			
			message.putAll(fanService.isFan(getJsonFromMessage(message), getUserFromMessage(message), request));
			return message.getMap();
		} catch (Exception e) {
			e.printStackTrace();
		}    
		message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		return message.getMap();
	}
	
	/**
	 * 获取我关注的对象列表
	 * @return
	 */
	@RequestMapping(value = "/myAttentions", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> myAttentionPaging(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		try {
			//{"id":1, "to_user_id": 2}
			if(!checkParams(message, request))
				return message.getMap();
			
			message.putAll(fanService.getMyAttentionsLimit(getJsonFromMessage(message), getUserFromMessage(message), request));
			return message.getMap();
		} catch (Exception e) {
			e.printStackTrace();
		}
		message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		return message.getMap();
	}
	
	/**
	 * 获取Ta关注的对象列表
	 * @return
	 */
	@RequestMapping(value = "/toAttentions", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> toAttentionPaging(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		try {
			//{"id":1, "to_user_id": 2}
			if(!checkParams(message, request))
				return message.getMap();
			
			message.putAll(fanService.getToAttentionsLimit(getJsonFromMessage(message), getUserFromMessage(message), request));
			return message.getMap();
		} catch (Exception e) {
			e.printStackTrace();
		}
		message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		return message.getMap();
	}
	
	/**
	 * 获取我的粉丝列表
	 * @return
	 */
	@RequestMapping(value = "/myFans", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> myFansPaging(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		try {
			//{"id":1, "to_user_id": 2}
			if(!checkParams(message, request))
				return message.getMap();
			
			message.putAll(fanService.getMyFansLimit(getJsonFromMessage(message), getUserFromMessage(message), request));
			return message.getMap();
		} catch (Exception e) {
			e.printStackTrace();
		}
		message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		return message.getMap();
	}
	
	/**
	 * 获取她的粉丝列表(关注信息将相对我来展示)
	 * @return
	 */
	@RequestMapping(value = "/toFans", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> toFansPaging(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		try {
			//{"id":1, "to_user_id": 2}
			if(!checkParams(message, request))
				return message.getMap();
			
			message.putAll(fanService.getToFansLimit(getJsonFromMessage(message), getUserFromMessage(message), request));
			return message.getMap();
		} catch (Exception e) {
			e.printStackTrace();
		}
		message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		return message.getMap();
	}
}
