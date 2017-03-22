package com.cn.leedane.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cn.leedane.model.FinancialBean;
import com.cn.leedane.service.FinancialService;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.ResponseMap;

/**
 * 记账控制器
 * @author LeeDane
 * 2016年7月22日 上午8:36:50
 * Version 1.0
 */
@RestController
@RequestMapping("/fn")
public class FinancialController extends BaseController{
	
	@Autowired
	private FinancialService<FinancialBean> financialService;
	
	/**
     * 客户端数据同步
     * @return 返回成功插入数据库的ID
     */
	@RequestMapping(value = "/financial", method = RequestMethod.POST)
    public Map<String, Object> save(HttpServletRequest request, HttpServletResponse response) {
    	ResponseMap message = new ResponseMap();
    	try{
    		if(!checkParams(message, request))
				return message.getMap();
			
			message.putAll(financialService.save(getJsonFromMessage(message), getUserFromMessage(message), request));
			return message.getMap();
		}catch(Exception e){
			e.printStackTrace();
		}
        message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		return message.getMap();
    }
	
	/**
     * 客户端数据更新
     * @return
     */
	@RequestMapping(value = "/financial", method = RequestMethod.PUT)
    public Map<String, Object> update(HttpServletRequest request, HttpServletResponse response) {
		ResponseMap message = new ResponseMap();
    	try{
    		if(!checkParams(message, request))
				return message.getMap();
			
			message.putAll(financialService.update(getJsonFromMessage(message), getUserFromMessage(message), request));
			return message.getMap();
		}catch(Exception e){
			e.printStackTrace();
		}
        message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		return message.getMap();
    }
	
	/**
     * 客户端数据删除
     * @return
     */
	@RequestMapping(value = "/financial", method = RequestMethod.DELETE)
    public Map<String, Object> delete(HttpServletRequest request, HttpServletResponse response) {
    	ResponseMap message = new ResponseMap();
    	try{
    		if(!checkParams(message, request))
				return message.getMap();
			
			message.putAll(financialService.delete(getJsonFromMessage(message), getUserFromMessage(message), request));
			return message.getMap();
		}catch(Exception e){
			e.printStackTrace();
		}
        message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		return message.getMap();
    }
	
	/**
     * 客户端数据同步
     * @return 返回成功同步的数量和有冲突的数据ID数组
     */
	@RequestMapping("/synchronous")
    public String synchronous(HttpServletRequest request, HttpServletResponse response) {
    	long startTime = System.currentTimeMillis();
    	Map<String, Object> message = new HashMap<String, Object>();
    	try{
    		if(!checkParams(message, request)){
				printWriter(message, response, startTime);
				return null;
			}
			message.putAll(financialService.synchronous(getJsonFromMessage(message), getUserFromMessage(message), request));
			printWriter(message, response, startTime);
			return null;
		}catch(Exception e){
			e.printStackTrace();
		}
        long endTime = System.currentTimeMillis();
        System.out.println("记账数据同步总计耗时："+ (endTime - startTime) +"毫秒");
        message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		printWriter(message, response, startTime);
		return null;
    }
    
    /**
     * 客户端强制更新数据(
     * 		在synchronous()后返回的冲突数据进行强制以客户端或者服务器端的为主，
     * 		要是以客户端的为主，将删掉服务器端的数据；
     * 		要是以服务器端的为主，将返回服务器端的数据，这时可以端需要做的就是替换掉客户端本地
     * 		数据为服务器端返回的数据。
     * )
     * 
     * @return
     */
	@RequestMapping("/force")
    public String force(HttpServletRequest request, HttpServletResponse response) {
    	long startTime = System.currentTimeMillis();
    	Map<String, Object> message = new HashMap<String, Object>();
    	try{
    		if(!checkParams(message, request)){
				printWriter(message, response, startTime);
				return null;
			}
			message.putAll(financialService.force(getJsonFromMessage(message), getUserFromMessage(message), request));
			printWriter(message, response, startTime);
			return null;
		}catch(Exception e){
			e.printStackTrace();
		}
        long endTime = System.currentTimeMillis();
        System.out.println("强制更新记账数据总计耗时："+ (endTime - startTime) +"毫秒");
        message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		printWriter(message, response, startTime);
		return null;
    }
	
	/**
	 * 获取该用户指定年份的数据
     * @return
     */
	@RequestMapping("/getByYear")
    public String getByYear(HttpServletRequest request, HttpServletResponse response) {
    	long startTime = System.currentTimeMillis();
    	Map<String, Object> message = new HashMap<String, Object>();
    	try{
    		if(!checkParams(message, request)){
				printWriter(message, response, startTime);
				return null;
			}
			message.putAll(financialService.getByYear(getJsonFromMessage(message), getUserFromMessage(message), request));
			printWriter(message, response, startTime);
			return null;
		}catch(Exception e){
			e.printStackTrace();
		}
        long endTime = System.currentTimeMillis();
        System.out.println("获取一年的总记账数据总计耗时："+ (endTime - startTime) +"毫秒");
        message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		printWriter(message, response, startTime);
		return null;
    }
	
	/**
	 * 获取该用户全部的数据
     * @return
     */
	@RequestMapping("/getAll")
    public String getAll(HttpServletRequest request, HttpServletResponse response) {
    	long startTime = System.currentTimeMillis();
    	Map<String, Object> message = new HashMap<String, Object>();
    	try{
    		if(!checkParams(message, request)){
				printWriter(message, response, startTime);
				return null;
			}
			message.putAll(financialService.getAll(getJsonFromMessage(message), getUserFromMessage(message), request));
			printWriter(message, response, startTime);
			return null;
		}catch(Exception e){
			e.printStackTrace();
		}
        long endTime = System.currentTimeMillis();
        System.out.println("获取全部总记账数据总计耗时："+ (endTime - startTime) +"毫秒");
        message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		printWriter(message, response, startTime);
		return null;
    }
	
	/**
	 * 搜索查询
     * @return
     */
	@RequestMapping(value = "/query", method = RequestMethod.GET)
    public Map<String, Object> query(HttpServletRequest request, HttpServletResponse response) {
    	ResponseMap message = new ResponseMap();
    	try{
    		if(!checkParams(message, request))
				return message.getMap();
			
			message.putAll(financialService.query(getJsonFromMessage(message), getUserFromMessage(message), request));
			return message.getMap();
		}catch(Exception e){
			e.printStackTrace();
		}
        message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		return message.getMap();
    }
	
	/**
	 * 分页查询
     * @return
     */
	@RequestMapping(value = "/financials", method = RequestMethod.GET)
    public Map<String, Object> paging(HttpServletRequest request, HttpServletResponse response) {
    	ResponseMap message = new ResponseMap();
    	try{
    		if(!checkParams(message, request))
				return message.getMap();
			
			message.putAll(financialService.paging(getJsonFromMessage(message), getUserFromMessage(message), request));
			return message.getMap();
		}catch(Exception e){
			e.printStackTrace();
		}
        message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		return message.getMap();
    }
}
