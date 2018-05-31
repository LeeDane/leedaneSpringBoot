package com.cn.leedane.springboot.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cn.leedane.controller.BaseController;
import com.cn.leedane.model.VisitorBean;
import com.cn.leedane.service.VisitorService;
import com.cn.leedane.utils.ControllerBaseNameUtil;

/**
 * 宝宝Html页面的控制器
 * @author LeeDane
 * 2018年5月30日 下午5:03:08
 * version 1.0
 */
@Controller
@RequestMapping(value = ControllerBaseNameUtil.baby)
public class BabyHtmlController extends BaseController{
	
	@Autowired
	private VisitorService<VisitorBean> visitorService;
	
	/***
	 * 下面的mapping会导致js/css文件依然访问到templates，返回的是html页面
	 * @param model
	 * @param httpSession
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String index(Model model, HttpServletRequest request){
		//首页不需要验证是否登录
		return index1(model, request);
	}
	
	@RequestMapping("/")
	public String index1(Model model, HttpServletRequest request){
		//检查权限，通过后台配置
		checkRoleOrPermission(model,request);	
		model.addAttribute("babyName", "小公主");
		return loginRoleCheck("baby/index", model, request);
	}
	
	@RequestMapping("/index")
	public String index2(Model model, HttpServletRequest request){
		return index1(model, request);
	}
}
