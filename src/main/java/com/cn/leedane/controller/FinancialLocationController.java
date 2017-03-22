package com.cn.leedane.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cn.leedane.model.FinancialLocationBean;
import com.cn.leedane.service.FinancialLocationService;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.ResponseMap;

/**
 * 记账位置信息控制器
 * @author LeeDane
 * 2016年11月22日 下午2:49:38
 * Version 1.0
 */
@RestController
@RequestMapping("/fn")
public class FinancialLocationController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());
	
	
	//记账位置信息
	@Autowired
	private FinancialLocationService<FinancialLocationBean> financialLocationService;

	/**
	 * 添加位置信息
	 * @return
	 */
	@RequestMapping(value = "/location", method = RequestMethod.POST)
	public String add(HttpServletRequest request, HttpServletResponse response){
		Map<String, Object> message = new HashMap<String, Object>();
		long start = System.currentTimeMillis();
		try {
			if(!checkParams(message, request)){
				printWriter(message, response, start);
				return null;
			}
			message.putAll(financialLocationService.add(getJsonFromMessage(message), getUserFromMessage(message), request));
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
	 * 更新位置信息
	 * @return
	 */
	@RequestMapping(value = "/location", method = RequestMethod.PUT)
	public String update(HttpServletRequest request, HttpServletResponse response){
		Map<String, Object> message = new HashMap<String, Object>();
		long start = System.currentTimeMillis();
		try {
			if(!checkParams(message, request)){
				printWriter(message, response, start);
				return null;
			}
			message.putAll(financialLocationService.update(getJsonFromMessage(message), getUserFromMessage(message), request));
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
	 * 删除位置信息
	 * @return
	 */
	@RequestMapping(value = "/location", method = RequestMethod.DELETE)
	public String delete(HttpServletRequest request, HttpServletResponse response){
		Map<String, Object> message = new HashMap<String, Object>();
		long start = System.currentTimeMillis();
		try {
			if(!checkParams(message, request)){
				printWriter(message, response, start);
				return null;
			}
			message.putAll(financialLocationService.delete(getJsonFromMessage(message), getUserFromMessage(message), request));
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
	 * 获取位置列表
	 * @return
	 */
	@RequestMapping(value = "/locations", method = RequestMethod.GET)
	public String paging(HttpServletRequest request, HttpServletResponse response){
		Map<String, Object> message = new HashMap<String, Object>();
		long start = System.currentTimeMillis();
		try {
			if(!checkParams(message, request)){
				printWriter(message, response, start);
				return null;
			}
			message.putAll(financialLocationService.paging(getJsonFromMessage(message), getUserFromMessage(message), request));
			printWriter(message, response, start);
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		printWriter(message, response, start);
		return null;
	}
	
	/**
	 * 获取所有的位置列表
	 * @return
	 */
	@RequestMapping(value = "/locations/all", method = RequestMethod.GET)
	public Map<String, Object> getAll(HttpServletRequest request, HttpServletResponse response){
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
