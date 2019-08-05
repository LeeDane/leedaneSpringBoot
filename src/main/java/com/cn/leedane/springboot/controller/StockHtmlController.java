package com.cn.leedane.springboot.controller;

import javax.servlet.http.HttpServletRequest;

import com.cn.leedane.utils.ConstantsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cn.leedane.controller.BaseController;
import com.cn.leedane.handler.stock.StockBuyHandler;
import com.cn.leedane.handler.stock.StockHandler;
import com.cn.leedane.handler.stock.StockSellHandler;
import com.cn.leedane.model.VisitorBean;
import com.cn.leedane.service.VisitorService;
import com.cn.leedane.utils.ControllerBaseNameUtil;

/**
 * 股票Html页面的控制器
 * @author LeeDane
 * 2018年6月21日 上午11:51:50
 * version 1.0
 */
@Controller
@RequestMapping(value = ControllerBaseNameUtil.stock)
public class StockHtmlController extends BaseController{
	
	@Autowired
	private VisitorService<VisitorBean> visitorService;
	
	@Autowired
	private StockHandler stockHandler;
	
	@Autowired
	private StockBuyHandler stockBuyHandler;
	
	@Autowired
	private StockSellHandler stockSellHandler;
	
	@RequestMapping("/")
	public String index(Model model, HttpServletRequest request){
		return index2(model, request);
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public String index2(Model model, HttpServletRequest request){
		//检查权限，通过后台配置
		checkRoleOrPermission(model,request);
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "进入股票模块首页", "", ConstantsUtil.STATUS_NORMAL, 0);
		return loginRoleCheck("stock/index", true, model, request);
	}
	
	@RequestMapping("/index")
	public String index1(Model model, HttpServletRequest request){
		return index2(model, request);
	}
}
