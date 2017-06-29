package com.cn.leedane.springboot.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
	
	/**
	 * 选择分类
	 * @param model
	 * @param type  类型，值可以是iframe或者其他
	 * @param request
	 * @return
	 */
	@RequestMapping("/select")
	public String select(Model model, 
			@RequestParam(value="rootId") int rootId, //根节点的名称
			HttpServletRequest request){
		model.addAttribute("rootId", rootId);
		return loginRoleCheck("category/select", true, model, request);
	}
}
