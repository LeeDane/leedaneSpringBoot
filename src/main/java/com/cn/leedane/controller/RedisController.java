package com.cn.leedane.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.ResponseMap;
import com.cn.leedane.utils.StringUtil;

@Controller
@RequestMapping(value = ControllerBaseNameUtil.rd)
public class RedisController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());
	/**
	 * 删除redis
	 * @return
	 */
	@RequestMapping(value = "/redis", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> delete(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		String key = getJsonFromMessage(message).getString("key");
		if(StringUtil.isNull(key)){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.缺少参数.value));
			message.put("responseCode", EnumUtil.ResponseCode.缺少参数.value);
			return message.getMap();
		}
		RedisUtil redisUtil = RedisUtil.getInstance();
		boolean result = redisUtil.delete(key);
		if(result){
			message.put("isSuccess", true);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.操作失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.操作失败.value);
		}
		return message.getMap();
	}
	
	/**
	 * 清除所有的缓存数据
	 * @return
	 */
	@RequestMapping(value = "/redis/clear", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> clearAll(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		RedisUtil redisUtil = RedisUtil.getInstance();
		boolean result = redisUtil.clearAll();
		if(result){
			message.put("isSuccess", true);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.操作失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.操作失败.value);
		}
		return message.getMap();
	}
}
