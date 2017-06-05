package com.cn.leedane.springboot.controller;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cn.leedane.controller.BaseController;
import com.cn.leedane.controller.RoleController;
import com.cn.leedane.controller.UserController;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.service.UserService;
import com.cn.leedane.utils.CommonUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.EnumUtil.ResponseCode;
import com.cn.leedane.utils.StringUtil;

/**
 * 商城Html页面的控制器
 * @author LeeDane
 * 2017年5月2日 下午5:00:32
 * version 1.0
 */
@Controller
@RequestMapping(value = ControllerBaseNameUtil.shop)
public class ShopHtmlController extends BaseController{
	
	@Autowired
	private UserService<UserBean> userService;
	
	/***
	 * 下面的mapping会导致js/css文件依然访问到templates，返回的是html页面
	 * @param model
	 * @param httpSession
	 * @return
	 */
	@RequestMapping("/")
	public String index1(Model model, HttpServletRequest request){
		//首页不需要验证是否登录
		return loginRoleCheck("shop/index", model, request);
	}
	
	@RequestMapping("/index")
	public String index2(Model model, HttpServletRequest request){
		return index1(model, request);
	}
}
