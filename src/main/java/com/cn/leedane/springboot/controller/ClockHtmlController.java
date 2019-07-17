package com.cn.leedane.springboot.controller;

import com.cn.leedane.controller.BaseController;
import com.cn.leedane.display.baby.IndexBabyDisplay;
import com.cn.leedane.handler.baby.BabyHandler;
import com.cn.leedane.model.VisitorBean;
import com.cn.leedane.model.baby.BabyBean;
import com.cn.leedane.service.VisitorService;
import com.cn.leedane.utils.CollectionUtil;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.DateUtil;
import com.cn.leedane.utils.StringUtil;
import com.cn.leedane.utils.baby.BabyUtil;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 任务提醒Html页面的控制器
 * @author LeeDane
 * 2019年1月27日 下午5:03:08
 * version 1.0
 */
//@Controller
//@RequestMapping(value = ControllerBaseNameUtil.clock)
public class ClockHtmlController extends BaseController{

	/***
	 * 下面的mapping会导致js/css文件依然访问到templates，返回的是html页面
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String index(Model model, HttpServletRequest request){
		return index2(0, model, request);
	}
	
	@RequestMapping("/")
	public String index1(Model model, HttpServletRequest request){
		return index2(0, model, request);
	}
	
	@RequestMapping("/{babyId}")
	public String index2(@PathVariable(value="babyId") int babyId, Model model, HttpServletRequest request){
		//检查权限，通过后台配置
		checkRoleOrPermission(model,request);
		return loginRoleCheck("baby/index", true, model, request);
	}
	
	@RequestMapping("/index")
	public String index2(Model model, HttpServletRequest request){
		return index1(model, request);
	}
	
	/**
	 * 打开捐赠页面
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/donate")
	public String donate(Model model, HttpServletRequest request){
		return loginRoleCheck("clock/donate", false, model, request);
	}
}
