package com.cn.leedane.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.StringUtil;

@Controller
@RequestMapping("/leedane/redis")
public class RedisController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());
	/**
	 * 添加赞
	 * @return
	 */
	@RequestMapping("/delete")
	public String delete(HttpServletRequest request, HttpServletResponse response){
		Map<String, Object> message = new HashMap<String, Object>();
		long start = System.currentTimeMillis();
		try {
			if(!checkParams(message, request)){
				printWriter(message, response, start);
				return null;
			}
			String key = getJsonFromMessage(message).getString("key");
			if(StringUtil.isNull(key)){
				message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.缺少参数.value));
				message.put("responseCode", EnumUtil.ResponseCode.缺少参数.value);
				printWriter(message, response, start);
				return null;
			}
			RedisUtil redisUtil = RedisUtil.getInstance();
			boolean result = redisUtil.delete(key);
			if(result){
				message.put("isSuccess", true);
			}else{
				message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.操作失败.value));
				message.put("responseCode", EnumUtil.ResponseCode.操作失败.value);
			}
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
	 * 清除所有的缓存数据
	 * @return
	 */
	@RequestMapping("/clearAll")
	public String clearAll(HttpServletRequest request, HttpServletResponse response){
		Map<String, Object> message = new HashMap<String, Object>();
		long start = System.currentTimeMillis();
		try {
			RedisUtil redisUtil = RedisUtil.getInstance();
			boolean result = redisUtil.clearAll();
			if(result){
				message.put("isSuccess", true);
			}else{
				message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.操作失败.value));
				message.put("responseCode", EnumUtil.ResponseCode.操作失败.value);
			}
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
