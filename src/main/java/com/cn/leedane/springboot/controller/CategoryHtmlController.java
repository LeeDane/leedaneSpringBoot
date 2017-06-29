package com.cn.leedane.springboot.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cn.leedane.controller.BaseController;
import com.cn.leedane.utils.ControllerBaseNameUtil;

/**
 * 分类管理Html页面的控制器
 * @author LeeDane
 * 2017年3月16日 上午11:23:13
 * Version 1.0
 */
@Controller
@RequestMapping(value = ControllerBaseNameUtil.cg)
public class CategoryHtmlController extends BaseController{
	
	/***
	 * 下面的mapping会导致js/css文件依然访问到templates，返回的是html页面
	 * @param model
	 * @param httpSession
	 * @return
	 */
	@RequestMapping()
	public String index(Model model, HttpServletRequest request){
		return index1(model, request);
	}
	
	@RequestMapping("/")
	public String index1(Model model, HttpServletRequest request){
		return loginRoleCheck("category/index", true, model, request);
	}
	
	@RequestMapping("/index")
	public String index2(Model model, HttpServletRequest request){
		return index1(model, request);
	}
}
