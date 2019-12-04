package com.cn.leedane.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cn.leedane.model.FinancialTwoLevelCategoryBean;
import com.cn.leedane.service.FinancialTwoCategoryService;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.ResponseMap;

/**
 * 记账二级分类控制器
 * @author LeeDane
 * 2016年12月8日 下午11:23:10
 * Version 1.0
 */
@RestController
@RequestMapping(value = ControllerBaseNameUtil.fnc)
public class FinancialTwoCategoryController extends BaseController{
	
	@Autowired
	private FinancialTwoCategoryService<FinancialTwoLevelCategoryBean> financialTwoCategoryService;
	
	/**
     * 获取二级分类的
     * @return 
     */
	@RequestMapping(value = "/twos", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
    public Map<String, Object> getAll(Model model, HttpServletRequest request) {
    	ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(financialTwoCategoryService.getAll(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
    }
}
