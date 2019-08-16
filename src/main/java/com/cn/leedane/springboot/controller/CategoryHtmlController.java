package com.cn.leedane.springboot.controller;

import javax.servlet.http.HttpServletRequest;

import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.EnumUtil;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cn.leedane.controller.BaseController;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.StringUtil;

/**
 * 分类管理Html页面的控制器
 * @author LeeDane
 * 2017年3月16日 上午11:23:13
 * Version 1.0
 */
@Controller
@RequestMapping(value = ControllerBaseNameUtil.cg)
public class CategoryHtmlController extends BaseController{

	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;

	/***
	 * 下面的mapping会导致js/css文件依然访问到templates，返回的是html页面
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping()
	public String index(Model model, HttpServletRequest request){
		return index1(model, request);
	}
	
	@RequestMapping("/")
	public String index1(Model model, HttpServletRequest request){
		checkRoleOrPermission(model, request);
		model.addAttribute("nonav", StringUtil.changeObjectToBoolean(SecurityUtils.getSubject().getSession().getAttribute("nonav")));
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "进入分类管理页面", "Category---->index()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.网页端.value);
		return loginRoleCheck("category/index", true, model, request);
	}
	
	@RequestMapping("/index")
	public String index2(Model model, HttpServletRequest request){
		return index1(model, request);
	}

	/**
	 * 选择分类
	 * @param model
	 * @param rootId
	 * @param request
	 * @return
	 */
	@RequestMapping("/select")
	public String select(Model model, 
			@RequestParam(value="rootId") int rootId, //根节点的名称
			HttpServletRequest request){
		checkRoleOrPermission(model, request);
		model.addAttribute("rootId", rootId);
		model.addAttribute("nonav", StringUtil.changeObjectToBoolean(SecurityUtils.getSubject().getSession().getAttribute("nonav")));
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "进入选择分类页面", "Category---->index()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.网页端.value);
		return loginRoleCheck("category/select", true, model, request);
	}
}
