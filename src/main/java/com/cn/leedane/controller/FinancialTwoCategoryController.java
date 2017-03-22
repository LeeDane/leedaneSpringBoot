package com.cn.leedane.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cn.leedane.model.FinancialTwoLevelCategoryBean;
import com.cn.leedane.service.FinancialTwoCategoryService;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.ResponseMap;

/**
 * 记账二级分类控制器
 * @author LeeDane
 * 2016年12月8日 下午11:23:10
 * Version 1.0
 */
@RestController
@RequestMapping("/fn/category")
public class FinancialTwoCategoryController extends BaseController{
	
	@Autowired
	private FinancialTwoCategoryService<FinancialTwoLevelCategoryBean> financialTwoCategoryService;
	
	/**
     * 获取二级分类的
     * @return 
     */
	@RequestMapping(value = "/twos", method = RequestMethod.GET)
    public Map<String, Object> getAll(HttpServletRequest request, HttpServletResponse response) {
    	ResponseMap message = new ResponseMap();
    	try{
    		if(!checkParams(message, request))
				return message.getMap();
			
			message.putAll(financialTwoCategoryService.getAll(getJsonFromMessage(message), getUserFromMessage(message), request));
			return message.getMap();
		}catch(Exception e){
			e.printStackTrace();
		}
        message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		return message.getMap();
    }
}
