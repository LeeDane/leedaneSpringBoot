package com.cn.leedane.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cn.leedane.model.FinancialOneLevelCategoryBean;
import com.cn.leedane.service.FinancialOneCategoryService;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.ResponseMap;

/**
 * 记账一级分类控制器
 * @author LeeDane
 * 2016年12月8日 下午11:19:03
 * Version 1.0
 */
@RestController
@RequestMapping(value = ControllerBaseNameUtil.fnc)
public class FinancialOneCategoryController extends BaseController{
	
	@Autowired
	private FinancialOneCategoryService<FinancialOneLevelCategoryBean> financialOneCategoryService;
	
	/**
     * 获取二级分类的
     * @return 
     */
	@RequestMapping(value = "/ones", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
    public Map<String, Object> getAll(HttpServletRequest request) {
    	ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		message.putAll(financialOneCategoryService.getAll(getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
    }
}
