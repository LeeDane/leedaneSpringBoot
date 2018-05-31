package com.cn.leedane.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cn.leedane.cache.SystemCache;
import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.ResponseMap;
import com.cn.leedane.utils.StringUtil;

/**
 * 缓存工具controller
 * @author LeeDane
 * 2017年12月24日 下午11:57:56
 * version 1.0
 */
@RestController
@RequestMapping(value = ControllerBaseNameUtil.cache)
public class CacheController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());
	
	@Autowired
	private SystemCache systemCache;
	
	/**
	 * 删除redis
	 * @return
	 */
	@RequestMapping(value = "/cache", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> delete(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		String key = getJsonFromMessage(message).getString("key");
		if(StringUtil.isNull(key)){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.缺少参数.value));
			message.put("responseCode", EnumUtil.ResponseCode.缺少参数.value);
			return message.getMap();
		}
		RedisUtil redisUtil = RedisUtil.getInstance();
		systemCache.removeCache(key);
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
	@RequestMapping(value = "/clearAll", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> clearAll(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		
		SystemCache.getSystemEhCache().clear();
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
