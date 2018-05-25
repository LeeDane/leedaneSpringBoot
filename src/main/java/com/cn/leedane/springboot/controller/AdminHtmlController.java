package com.cn.leedane.springboot.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cn.leedane.controller.BaseController;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.mall.S_ProductBean;
import com.cn.leedane.service.UserService;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.EnumUtil.ProductPlatformType;

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
	
	/****************   商品管理          ***********************/
	@RequestMapping(ControllerBaseNameUtil.adsh + "/home")
	public String shHome(Model model, HttpSession httpSession, HttpServletRequest request){
		return loginRoleCheck("admin/mall/home", true, model, httpSession, request);
	}
	
	@RequestMapping(ControllerBaseNameUtil.adsh + "/product")
	public String shProduct(Model model, HttpSession httpSession, HttpServletRequest request){
		return loginRoleCheck("admin/mall/product", true, model, httpSession, request);
	}
	
	@RequestMapping(ControllerBaseNameUtil.adsh + "/product-add")
	public String shProductAdd(Model model, HttpSession httpSession, HttpServletRequest request){
		S_ProductBean productBean = new S_ProductBean();
		model.addAttribute("product", productBean);
		
		List<String> platforms = new ArrayList<String>();
		for(ProductPlatformType type: EnumUtil.ProductPlatformType.values()){
			platforms.add(type.value);
		}
		model.addAttribute("platforms", platforms);
		return loginRoleCheck("admin/mall/product-add", true, model, httpSession, request);
	}
	
	/****************   系统设置--->任务管理          ***********************/
	@RequestMapping(ControllerBaseNameUtil.adst + "/job")
	public String stJob(Model model, HttpSession httpSession, HttpServletRequest request){
		return loginRoleCheck("admin/setting/job", true, model, httpSession, request);
	}
	
	@RequestMapping(ControllerBaseNameUtil.adst + "/clearCache")
	public String stClearCache(Model model, HttpSession httpSession, HttpServletRequest request){
		return loginRoleCheck("admin/setting/clearCache", true, model, httpSession, request);
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
	
	
}
