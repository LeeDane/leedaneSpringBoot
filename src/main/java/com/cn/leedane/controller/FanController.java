package com.cn.leedane.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cn.leedane.model.FanBean;
import com.cn.leedane.service.FanService;
import com.cn.leedane.utils.EnumUtil;

@Controller
@RequestMapping("/leedane/fan")
public class FanController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());
	@Autowired
	private FanService<FanBean> fanService;
	
	/**
	 * 取消关注
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/cancel")
	public String cancel(HttpServletRequest request, HttpServletResponse response){
		Map<String, Object> message = new HashMap<String, Object>();
		long start = System.currentTimeMillis();
		try {
			//{"toUserId": 2}
			if(!checkParams(message, request)){
				printWriter(message, response, start);
				return null;
			}
			message.put("isSuccess", fanService.cancel(getJsonFromMessage(message), getUserFromMessage(message), request));
			printWriter(message, response, start);
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		printWriter(message, response, start);
		return null;
	}
	/**
	 * 添加关注
	 * @return
	 */
	@RequestMapping("/add")
	public String add(HttpServletRequest request, HttpServletResponse response){
		Map<String, Object> message = new HashMap<String, Object>();
		long start = System.currentTimeMillis();
		try {
			//{"id":1, "to_user_id": 2}
			if(!checkParams(message, request)){
				printWriter(message, response, start);
				return null;
			}
			message.putAll(fanService.addFan(getJsonFromMessage(message), getUserFromMessage(message), request));
			printWriter(message, response, start);
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		printWriter(message, response, start);
		return null;
	}
	
	/**
	 * 判读两人是否是好友
	 * @return
	 */
	@RequestMapping("/isFan")
	public String isFan(HttpServletRequest request, HttpServletResponse response){
		Map<String, Object> message = new HashMap<String, Object>();
		long start = System.currentTimeMillis();
		try {
			//{"id":1, "to_user_id": 2}
			if(!checkParams(message, request)){
				printWriter(message, response, start);
				return null;
			}
			message.putAll(fanService.isFan(getJsonFromMessage(message), getUserFromMessage(message), request));
			printWriter(message, response, start);
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}    
		message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		printWriter(message, response, start);
		return null;
	}
	
	/**
	 * 获取我关注的对象列表
	 * @return
	 */
	@RequestMapping("/myAttentionPaging")
	public String myAttentionPaging(HttpServletRequest request, HttpServletResponse response){
		Map<String, Object> message = new HashMap<String, Object>();
		long start = System.currentTimeMillis();
		try {
			//{"id":1, "to_user_id": 2}
			if(!checkParams(message, request)){
				printWriter(message, response, start);
				return null;
			}
			message.putAll(fanService.getMyAttentionsLimit(getJsonFromMessage(message), getUserFromMessage(message), request));
			printWriter(message, response, start);
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		printWriter(message, response, start);
		return null;
	}
	
	/**
	 * 获取Ta关注的对象列表
	 * @return
	 */
	@RequestMapping("/toAttentionPaging")
	public String toAttentionPaging(HttpServletRequest request, HttpServletResponse response){
		Map<String, Object> message = new HashMap<String, Object>();
		long start = System.currentTimeMillis();
		try {
			//{"id":1, "to_user_id": 2}
			if(!checkParams(message, request)){
				printWriter(message, response, start);
				return null;
			}
			message.putAll(fanService.getToAttentionsLimit(getJsonFromMessage(message), getUserFromMessage(message), request));
			printWriter(message, response, start);
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		printWriter(message, response, start);
		return null;
	}
	
	/**
	 * 获取我的粉丝列表
	 * @return
	 */
	@RequestMapping("/myFansPaging")
	public String myFansPaging(HttpServletRequest request, HttpServletResponse response){
		Map<String, Object> message = new HashMap<String, Object>();
		long start = System.currentTimeMillis();
		try {
			//{"id":1, "to_user_id": 2}
			if(!checkParams(message, request)){
				printWriter(message, response, start);
				return null;
			}
			message.putAll(fanService.getMyFansLimit(getJsonFromMessage(message), getUserFromMessage(message), request));
			printWriter(message, response, start);
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		printWriter(message, response, start);
		return null;
	}
	
	/**
	 * 获取她的粉丝列表(关注信息将相对我来展示)
	 * @return
	 */
	@RequestMapping("/toFansPaging")
	public String toFansPaging(HttpServletRequest request, HttpServletResponse response){
		Map<String, Object> message = new HashMap<String, Object>();
		long start = System.currentTimeMillis();
		try {
			//{"id":1, "to_user_id": 2}
			if(!checkParams(message, request)){
				printWriter(message, response, start);
				return null;
			}
			message.putAll(fanService.getToFansLimit(getJsonFromMessage(message), getUserFromMessage(message), request));
			printWriter(message, response, start);
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		printWriter(message, response, start);
		return null;
	}
}
