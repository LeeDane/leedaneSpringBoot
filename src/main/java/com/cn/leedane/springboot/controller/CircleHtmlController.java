package com.cn.leedane.springboot.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cn.leedane.controller.BaseController;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.service.UserService;
import com.cn.leedane.utils.ControllerBaseNameUtil;

/**
 * 圈子Html页面的控制器
 * @author LeeDane
 * 2017年5月31日 上午10:27:00
 * version 1.0
 */
@Controller
@RequestMapping(value = ControllerBaseNameUtil.cc)
public class CircleHtmlController extends BaseController{
	
	@Autowired
	private UserService<UserBean> userService;
	
	/***
	 * 下面的mapping会导致js/css文件依然访问到templates，返回的是html页面
	 * @param model
	 * @param httpSession
	 * @return
	 */
	@RequestMapping()
	public String index(Model model, HttpServletRequest request){
		//首页不需要验证是否登录
		return index1(model, request);
	}
	
	@RequestMapping("/")
	public String index1(Model model, HttpServletRequest request){
		//首页不需要验证是否登录
		return loginRoleCheck("circle/index", model, request);
	}
	
	@RequestMapping("/index")
	public String index2(Model model, HttpServletRequest request){
		return index1(model, request);
	}
}
