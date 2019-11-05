package com.cn.leedane.springboot.controller;

import com.cn.leedane.controller.BaseController;
import com.cn.leedane.handler.MoodHandler;
import com.cn.leedane.model.FilePathBean;
import com.cn.leedane.model.VisitorBean;
import com.cn.leedane.service.AppVersionService;
import com.cn.leedane.service.VisitorService;
import com.cn.leedane.springboot.ElasticSearchUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.EnumUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 信纸Html页面的控制器
 * @author LeeDane
 * 2019年8月30日 上午12:23:13
 * Version 1.0
 */
@Controller
@RequestMapping(value = ControllerBaseNameUtil.paper)
public class PaperController extends BaseController{
	
	@Autowired
	private VisitorService<VisitorBean> visitorService;
	
	@Autowired
	private AppVersionService<FilePathBean> appVersionService;

	@Autowired
	private MoodHandler moodHandler;

	@Autowired
	private ElasticSearchUtil elasticSearchUtil;
	
	/***
	 * 下面的mapping会导致js/css文件依然访问到templates，返回的是html页面
	 * @param model
	 * @param request
	 * @return
	 */
	/*@RequestMapping
	public String index(Model model){
		return "index";
	}*/
	
	@RequestMapping("/index")
	public String index1(Model model, HttpServletRequest request){

		Context context = new Context();
		Map<String, Object> params = new HashMap<>();
		params.put("title", "好烦好烦好烦好烦");
		context.setVariables(params);
		TemplateEngine engine = new TemplateEngine();
		String template = "信纸首页\n" +
				"\t\t<button id=\"opened\" type=\"button\" class=\"layui-btn layui-btn-normal layui-btn-radius\" onclick=\"openLink('/paper/opened/1');\">查看信纸</button>";
		String myHtml = engine.process(template, context);
		model.addAttribute("myHtml", myHtml);
		//首页不需要验证是否登录
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "进入信纸首页", "", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.网页端.value);
		return loginRoleCheck(ControllerBaseNameUtil.paper + "/index", true, model, request);
	}

	/**
	 * 打开信纸
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/write")
	public String write(Model model, HttpServletRequest request){
		//首页不需要验证是否登录
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "写信页面", "", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.网页端.value);
		return loginRoleCheck(ControllerBaseNameUtil.paper + "/write", true, model, request);
	}

	/**
	 * 打开信纸
	 * @param paperId 信纸的唯一ID
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/opened/{paperId}")
	public String opened(@PathVariable(value="paperId") String paperId, Model model, HttpServletRequest request){
		//首页不需要验证是否登录
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "查看信纸信纸首页", "", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.网页端.value);
		model.addAttribute("id", paperId);
		return loginRoleCheck(ControllerBaseNameUtil.paper + "/opened", true, model, request);
	}
}
