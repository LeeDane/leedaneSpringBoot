package com.cn.leedane.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cn.leedane.model.FinancialLocationBean;
import com.cn.leedane.service.FinancialLocationService;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.ResponseMap;

/**
 * 记账位置信息控制器
 * @author LeeDane
 * 2016年11月22日 下午2:49:38
 * Version 1.0
 */
@RestController
@RequestMapping(value = ControllerBaseNameUtil.fn)
public class FinancialLocationController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());
	
	
	//记账位置信息
	@Autowired
	private FinancialLocationService<FinancialLocationBean> financialLocationService;

	/**
	 * 添加位置信息
	 * @return
	 */
	@RequestMapping(value = "/location", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> add(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		try {
			if(!checkParams(message, request))
				return message.getMap();
			
			message.putAll(financialLocationService.add(getJsonFromMessage(message), getUserFromMessage(message), request));
			return message.getMap();
		} catch (Exception e) {
			e.printStackTrace();
		}
		message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		return message.getMap();
	}
	
	/**
	 * 更新位置信息
	 * @return
	 */
	@RequestMapping(value = "/location", method = RequestMethod.PUT, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> update(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		try {
			if(!checkParams(message, request))
				return message.getMap();
			
			message.putAll(financialLocationService.update(getJsonFromMessage(message), getUserFromMessage(message), request));
			return message.getMap();
		} catch (Exception e) {
			e.printStackTrace();
		}
		message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		return message.getMap();
	}
	
	/**
	 * 删除位置信息
	 * @return
	 */
	@RequestMapping(value = "/location", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> delete(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		try {
			if(!checkParams(message, request))
				return message.getMap();
			
			message.putAll(financialLocationService.delete(getJsonFromMessage(message), getUserFromMessage(message), request));
			return message.getMap();
		} catch (Exception e) {
			e.printStackTrace();
		}
		message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		return message.getMap();
	}
	
	/**
	 * 获取位置列表
	 * @return
	 */
	@RequestMapping(value = "/locations", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> paging(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		try {
			if(!checkParams(message, request))
				return message.getMap();
			
			message.putAll(financialLocationService.paging(getJsonFromMessage(message), getUserFromMessage(message), request));
			return message.getMap();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return message.getMap();
	}
	
	/**
	 * 获取所有的位置列表
	 * @return
	 */
	@RequestMapping(value = "/locations/all", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> getAll(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		try {
			if(!checkParams(message, request))
				return message.getMap();
			
			message.putAll(financialLocationService.getAll(getJsonFromMessage(message), getUserFromMessage(message), request));
			return message.getMap();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return message.getMap();
	}
}
