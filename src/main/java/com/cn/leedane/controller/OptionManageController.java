package com.cn.leedane.controller;

import com.cn.leedane.model.OptionBean;
import com.cn.leedane.service.OptionManageService;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.ResponseMap;
import org.apache.ibatis.annotations.Param;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/***
 * 选项管理controller
 */
@RestController
@RequestMapping(value = ControllerBaseNameUtil.option)
public class OptionManageController extends BaseController{
	
	@Autowired
	private OptionManageService<OptionBean> optionManageService;
	
	/**
	 * 添加option
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> add(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(optionManageService.add(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 修改option
	 * @return
	 */
	@RequestMapping(method = RequestMethod.PUT, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> update(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(optionManageService.update(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 删除option
	 * @return
	 */
	@RequestMapping(value = "/{optionId}", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> delete(@PathVariable("optionId") long optionId, Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(optionManageService.delete(optionId, getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 分页获取option列表
	 * @return
	 */
	@RequestMapping(value = "/options", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> paging(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(optionManageService.paging(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 批量删除option
	 * @return
	 * @throws SchedulerException 
	 */
	@RequestMapping(value = "/options", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> deletes(Model model, HttpServletRequest request, @Param("optionIds") String optionIds){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(optionManageService.deletes(optionIds, getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
}
