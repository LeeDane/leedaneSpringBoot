package com.cn.leedane.springboot.controller;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
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
 * 后台管理Html页面的控制器
 * @author LeeDane
 * 2017年3月21日 下午6:23:13
 * Version 1.0
 */
@Controller
public class AdminHtmlController extends BaseController{
	private Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private UserService<UserBean> userService;
	
	/***
	 * 下面的mapping会导致js/css文件依然访问到templates，返回的是html页面
	 * @param model
	 * @param httpSession
	 * @return
	 */
	/*@RequestMapping
	public String index(Model model, HttpSession httpSession){
		return "index";
	}*/
	
	@RequestMapping(ControllerBaseNameUtil.ad +"/")
	public String index1(Model model, HttpSession httpSession, HttpServletRequest request){
		//首页不需要验证是否登录
		return loginRoleCheck("admin/index", model, httpSession, request);
	}
	
	@RequestMapping(ControllerBaseNameUtil.ad + "/index")
	public String index2(Model model, HttpSession httpSession, HttpServletRequest request){
		return index1(model, httpSession, request);
	}
	
	@RequestMapping(ControllerBaseNameUtil.adwc + "/welcome")
	public String wcWelcome(Model model, HttpSession httpSession, HttpServletRequest request){
		return loginRoleCheck("admin/welcome/welcome", true, model, httpSession, request);
	}
	
	@RequestMapping(ControllerBaseNameUtil.adwc + "/loginHistory")
	public String wcLoginHistory(Model model, HttpSession httpSession, HttpServletRequest request){
		return loginRoleCheck("admin/welcome/loginHistory", true, model, httpSession, request);
	}
	
	@RequestMapping(ControllerBaseNameUtil.adwc + "/about")
	public String wcAbout(Model model, HttpSession httpSession, HttpServletRequest request){
		return loginRoleCheck("admin/welcome/about", false, model, httpSession, request);
	}
	
	@RequestMapping(ControllerBaseNameUtil.adwc + "/contact")
	public String wcContact(Model model, HttpSession httpSession, HttpServletRequest request){
		return loginRoleCheck("admin/welcome/contact", false, model, httpSession, request);
	}
	
	@RequestMapping(ControllerBaseNameUtil.adwc + "/download")
	public String wcDownload(Model model, HttpSession httpSession, HttpServletRequest request){
		return loginRoleCheck("admin/welcome/download", false, model, httpSession, request);
	}
	
	@RequestMapping(ControllerBaseNameUtil.adus + "/search")
	public String usSearch(Model model, HttpSession httpSession, HttpServletRequest request){
		return loginRoleCheck("admin/user/search", true, model, httpSession, request);
	}
	
	@RequestMapping(ControllerBaseNameUtil.adus + "/new")
	public String usNew(Model model, HttpSession httpSession, HttpServletRequest request){
		return loginRoleCheck("admin/user/new", true, model, httpSession, request);
	}
	
	@RequestMapping(ControllerBaseNameUtil.adus + "/black")
	public String usBlack(Model model, HttpSession httpSession, HttpServletRequest request){
		return loginRoleCheck("admin/user/black", true, model, httpSession, request);
	}
	
	
	@RequestMapping(ControllerBaseNameUtil.adbg + "/check")
	public String bgCheck(Model model, HttpSession httpSession, HttpServletRequest request){
		return loginRoleCheck("admin/blog/check", true, model, httpSession, request);
	}
	
	/****************    权限管理          ***********************/
	@RequestMapping(ControllerBaseNameUtil.adpm + "/permission")
	public String pmPermission(Model model, HttpSession httpSession, HttpServletRequest request){
		return loginRoleCheck("admin/permission/permission", true, model, httpSession, request);
	}
	
	@RequestMapping(ControllerBaseNameUtil.adpm + "/impowerPermission")
	public String pmImpowerPermission(Model model, HttpSession httpSession, HttpServletRequest request){
		return loginRoleCheck("admin/permission/impowerRole", true, model, httpSession, request);
	}
	
	/****************    角色管理          ***********************/
	@RequestMapping(ControllerBaseNameUtil.adpm + "/role")
	public String pmRole(Model model, HttpSession httpSession, HttpServletRequest request){
		return loginRoleCheck("admin/permission/role", true, model, httpSession, request);
	}
	
	/****************   链接管理          ***********************/
	@RequestMapping(ControllerBaseNameUtil.adpm + "/link")
	public String pmLink(Model model, HttpSession httpSession, HttpServletRequest request){
		return loginRoleCheck("admin/permission/link", true, model, httpSession, request);
	}
	
	/****************   系统设置--->任务管理          ***********************/
	@RequestMapping(ControllerBaseNameUtil.adst + "/job")
	public String stJob(Model model, HttpSession httpSession, HttpServletRequest request){
		return loginRoleCheck("admin/setting/job", true, model, httpSession, request);
	}
	
	/**
	 * 校验地址，不校验是否登录
	 * @param urlParse
	 * @param model
	 * @param httpSession
	 * @return
	 */
	public String loginRoleCheck(String urlParse, Model model, HttpSession httpSession, HttpServletRequest request){
		return loginRoleCheck(urlParse, false, model, httpSession, request);
	}
	
	/**
	 * 校验地址，校验是否登录
	 * @param urlParse
	 * @param mustAdmin 为true表示必须是管理员身份登录，不然就跳转到登录页面
	 * @param model
	 * @param httpSession
	 * @return
	 */
	public String loginRoleCheck(String urlParse, boolean mustAdmin, Model model, HttpSession httpSession, HttpServletRequest request){
		//设置统一的请求模式
		model.addAttribute("isDebug", ConstantsUtil.IS_DEBUG);
		Object obj = httpSession.getAttribute(UserController.USER_INFO_KEY);
		UserBean userBean = null;
		String account = "";
		boolean isLogin = false;
		if(obj != null){
			logger.info("obj不为空");
			isLogin = !isLogin;
			userBean = (UserBean)obj;

			//获取当前的Subject  
	        Subject currentUser = SecurityUtils.getSubject();
			//后台只有管理员权限才能操作
			if(currentUser.hasRole(RoleController.ADMIN_ROLE_CODE)){
				isLogin = !isLogin;
				account = userBean.getAccount();
				model.addAttribute("isLogin", isLogin);
				model.addAttribute("account", account);
			}else{
				httpSession.removeAttribute(UserController.USER_INFO_KEY);
				model.addAttribute("errorMessage", EnumUtil.getResponseValue(ResponseCode.请使用有管理员权限的账号登录.value));
				return "redirect:/lg?errorcode=" +EnumUtil.ResponseCode.请使用有管理员权限的账号登录.value +"&t="+ UUID.randomUUID().toString() +"&ref="+ CommonUtil.getFullPath(request);
			}
		}else{
			logger.info("obj为空");
			model.addAttribute("errorMessage", EnumUtil.getResponseValue(ResponseCode.请先登录.value));
			return "redirect:/lg?errorcode="+ EnumUtil.ResponseCode.请先登录.value +"&ref="+ CommonUtil.getFullPath(request) +"&t="+ UUID.randomUUID().toString();
		}
		
		return StringUtil.isNotNull(urlParse) ? urlParse : "404";
	}
}
