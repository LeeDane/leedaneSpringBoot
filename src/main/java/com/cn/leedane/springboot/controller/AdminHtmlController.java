package com.cn.leedane.springboot.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cn.leedane.controller.BaseController;
import com.cn.leedane.model.mall.S_ProductBean;
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
	public String index1(Model model, HttpServletRequest request){
		//首页不需要验证是否登录
		return adminLoginRoleCheck("admin/index", model, request);
	}
	
	@RequestMapping(ControllerBaseNameUtil.ad + "/index")
	public String index2(Model model, HttpServletRequest request){
		return index1(model, request);
	}
	
	@RequestMapping(ControllerBaseNameUtil.adwc + "/welcome")
	public String wcWelcome(Model model, HttpServletRequest request){
		return adminLoginRoleCheck("admin/welcome/welcome", model, request);
	}
	
	@RequestMapping(ControllerBaseNameUtil.adwc + "/loginHistory")
	public String wcLoginHistory(Model model, HttpServletRequest request){
		return adminLoginRoleCheck("admin/welcome/loginHistory", model, request);
	}
	
	@RequestMapping(ControllerBaseNameUtil.adwc + "/about")
	public String wcAbout(Model model, HttpServletRequest request){
		return adminLoginRoleCheck("admin/welcome/about", model, request);
	}
	
	@RequestMapping(ControllerBaseNameUtil.adwc + "/contact")
	public String wcContact(Model model, HttpServletRequest request){
		return adminLoginRoleCheck("admin/welcome/contact", model, request);
	}
	
	@RequestMapping(ControllerBaseNameUtil.adwc + "/download")
	public String wcDownload(Model model, HttpServletRequest request){
		return adminLoginRoleCheck("admin/welcome/download", model, request);
	}
	
	@RequestMapping(ControllerBaseNameUtil.adus + "/search")
	public String usSearch(Model model, HttpServletRequest request){
		return adminLoginRoleCheck("admin/user/search", model, request);
	}
	
	@RequestMapping(ControllerBaseNameUtil.adus + "/new")
	public String usNew(Model model, HttpServletRequest request){
		return adminLoginRoleCheck("admin/user/new", model, request);
	}
	
	@RequestMapping(ControllerBaseNameUtil.adus + "/black")
	public String usBlack(Model model, HttpServletRequest request){
		return adminLoginRoleCheck("admin/user/black", model, request);
	}
	
	
	@RequestMapping(ControllerBaseNameUtil.adbg + "/check")
	public String bgCheck(Model model, HttpServletRequest request){
		return adminLoginRoleCheck("admin/blog/check", model, request);
	}
	
	/****************    权限管理          ***********************/
	@RequestMapping(ControllerBaseNameUtil.adpm + "/permission")
	public String pmPermission(Model model, HttpServletRequest request){
		return adminLoginRoleCheck("admin/permission/permission", model, request);
	}
	
	@RequestMapping(ControllerBaseNameUtil.adpm + "/impowerPermission")
	public String pmImpowerPermission(Model model, HttpServletRequest request){
		return adminLoginRoleCheck("admin/permission/impowerRole", model, request);
	}
	
	/****************    角色管理          ***********************/
	@RequestMapping(ControllerBaseNameUtil.adpm + "/role")
	public String pmRole(Model model, HttpServletRequest request){
		return adminLoginRoleCheck("admin/permission/role", model, request);
	}
	
	/****************   链接管理          ***********************/
	@RequestMapping(ControllerBaseNameUtil.adpm + "/link")
	public String pmLink(Model model, HttpServletRequest request){
		return adminLoginRoleCheck("admin/permission/link", model, request);
	}
	
	/****************   商品管理          ***********************/
	@RequestMapping(ControllerBaseNameUtil.adsh + "/home")
	public String shHome(Model model, HttpServletRequest request){
		return adminLoginRoleCheck("admin/mall/home", model, request);
	}
	
	@RequestMapping(ControllerBaseNameUtil.adsh + "/product")
	public String shProduct(Model model, HttpServletRequest request){
		return adminLoginRoleCheck("admin/mall/product", model, request);
	}
	
	@RequestMapping(ControllerBaseNameUtil.adsh + "/product-add")
	public String shProductAdd(Model model, HttpServletRequest request){
		S_ProductBean productBean = new S_ProductBean();
		model.addAttribute("product", productBean);
		
		List<String> platforms = new ArrayList<String>();
		for(ProductPlatformType type: EnumUtil.ProductPlatformType.values()){
			platforms.add(type.value);
		}
		model.addAttribute("platforms", platforms);
		return adminLoginRoleCheck("admin/mall/product-add", model, request);
	}
	
	/****************   系统设置--->任务管理          ***********************/
	@RequestMapping(ControllerBaseNameUtil.adst + "/job")
	public String stJob(Model model, HttpServletRequest request){
		return adminLoginRoleCheck("admin/setting/job", model, request);
	}
	
	@RequestMapping(ControllerBaseNameUtil.adst + "/clearCache")
	public String stClearCache(Model model, HttpServletRequest request){
		return adminLoginRoleCheck("admin/setting/clearCache", model, request);
	}
	
	/**
	 * 校验地址，不校验是否登录
	 * @param urlParse
	 * @param model
	 * @param httpSession
	 * @return
	 */
	public String loginRoleCheck(String urlParse, Model model, HttpServletRequest request){
		return adminLoginRoleCheck(urlParse, model, request);
	}
	
	
}
