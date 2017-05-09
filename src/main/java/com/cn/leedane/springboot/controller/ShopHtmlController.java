package com.cn.leedane.springboot.controller;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
	
	/**
	 * 校验地址，不校验是否登录
	 * @param urlParse
	 * @param model
	 * @param httpSession
	 * @return
	 */
	public String loginRoleCheck(String urlParse, Model model, HttpServletRequest request){
		return loginRoleCheck(urlParse, false, model, request);
	}
	
	/**
	 * 校验地址，校验是否登录
	 * @param urlParse
	 * @param mustLogin 为true表示必须登录，不然就跳转到登录页面
	 * @param model
	 * @param httpSession
	 * @return
	 */
	public String loginRoleCheck(String urlParse, boolean mustLogin, Model model, HttpServletRequest request){
		//设置统一的请求模式
		model.addAttribute("isDebug", false);
		Object o = null;
		//获取当前的Subject  
        Subject currentUser = SecurityUtils.getSubject();
        if(currentUser.isAuthenticated()){
        	o = currentUser.getSession().getAttribute(UserController.USER_INFO_KEY);
        }
		
		boolean isLogin = false;
		boolean isAdmin = false;
		if(o != null){
			isLogin = true;
			UserBean user = (UserBean)o;
			isAdmin = currentUser.hasRole(RoleController.ADMIN_ROLE_CODE);
			model.addAttribute("account", user.getAccount());
			model.addAttribute("loginUserId", user.getId());
		}
		model.addAttribute("isLogin",  isLogin);
		model.addAttribute("isAdmin", isAdmin);
		if(mustLogin && !isLogin){
			model.addAttribute("errorMessage", EnumUtil.getResponseValue(ResponseCode.请先登录.value));
			return "redirect:/lg?errorcode="+ EnumUtil.ResponseCode.请先登录.value +"&ref="+ CommonUtil.getFullPath(request) +"&t="+ UUID.randomUUID().toString();
		}
		
		return StringUtil.isNotNull(urlParse) ? urlParse : "404";
	}
}
