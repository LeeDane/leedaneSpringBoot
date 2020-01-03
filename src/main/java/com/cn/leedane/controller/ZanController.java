package com.cn.leedane.controller;

import com.cn.leedane.model.ZanBean;
import com.cn.leedane.service.ZanService;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.ResponseMap;
import com.cn.leedane.utils.ResponseModel;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = ControllerBaseNameUtil.lk)  //like的意思
public class ZanController extends BaseController{

	private Logger logger = Logger.getLogger(getClass());
	//赞service
	@Autowired
	private ZanService<ZanBean> zanService;

	/**
	 * 添加赞
	 * @return
	 */
	@RequestMapping(value = "/zan", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public ResponseModel add(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getModel();
		checkRoleOrPermission(model, request);
		return zanService.addZan(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request));
	}
	
	/**
	 * 取消赞
	 * @return
	 */
	@RequestMapping(value = "/zan", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"})
	public ResponseModel delete(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getModel();
		checkRoleOrPermission(model, request);
		return zanService.deleteZan(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request));
	}
	
	/**
	 * 获取赞列表
	 * @return
	 */
	@RequestMapping(value = "/zans", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public ResponseModel paging(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getModel();
		checkRoleOrPermission(model, request);
		return zanService.getLimit(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request));
	}
	
	/**
	 * 获取点赞用户列表
	 * @return
	 */
	@RequestMapping(value = "/allZanUsers", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public ResponseModel getAllZanUser(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getModel();
		checkRoleOrPermission(model, request);
		return zanService.getAllZanUser(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request));
	}
}
