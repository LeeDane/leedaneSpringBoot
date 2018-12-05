package com.cn.leedane.controller.clock;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cn.leedane.controller.BaseController;
import com.cn.leedane.model.clock.ClockBean;
import com.cn.leedane.service.clock.ClockService;
import com.cn.leedane.utils.CommonUtil;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.ResponseMap;

/**
 * 提醒接口controller
 * @author LeeDane
 * 2018年8月29日 上午10:29:37
 * version 1.0
 */
@RestController
@RequestMapping(value = ControllerBaseNameUtil.clock)
public class ClockController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());

	@Autowired
	private ClockService<ClockBean> clockService;
		
	/**
	 * 添加任务
	 * @return
	 */
	@RequestMapping(value = "/clock", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> add(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(clockService.add(getJsonFromMessage(message), getMustLoginUserFromShiro(), request));
		return message.getMap();
	}
	
	/**
	 * 修改任务
	 * @return
	 */
	@RequestMapping(value = "/{clockId}", method = RequestMethod.PUT, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> update(@PathVariable("clockId") int clockId, Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(clockService.update(clockId, getJsonFromMessage(message), getMustLoginUserFromShiro(), request));
		return message.getMap();
	}
	/**
	 * 获取任务的信息
	 * @return
	 */
	@RequestMapping(value = "/{clockId}/info", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> getClock(@PathVariable("clockId") int clockId, Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(clockService.getClock(clockId, getJsonFromMessage(message), getMustLoginUserFromShiro(), request));
		return message.getMap();
	}
	
	/**
	 * 获取任务的缩略信息
	 * @return
	 */
	@RequestMapping(value = "/{clockId}/thumbnail", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> getClockThumbnail(@PathVariable("clockId") int clockId, Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(clockService.getClockThumbnail(clockId, getJsonFromMessage(message), getMustLoginUserFromShiro(), request));
		return message.getMap();
	}
	
	
	/**
	 * 搜索任务(只支持搜索共享的任务或者是自己的任务，返回最多10条记录)
	 * @return
	 */
	@RequestMapping(value = "/search", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> search(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(clockService.search(getJsonFromMessage(message), getMustLoginUserFromShiro(), request));
		return message.getMap();
	}
//	
//	/**
//	 * 修改圈子
//	 * @return
//	 */
//	@RequestMapping(value = "/{circleId}", method = RequestMethod.PUT, produces = {"application/json;charset=UTF-8"})
//	public Map<String, Object> update(@PathVariable("circleId") int circleId, Model model, HttpServletRequest request){
//		ResponseMap message = new ResponseMap();
//		if(!checkParams(message, request))
//			return message.getMap();
//		
//		checkRoleOrPermission(model, request);
//		message.putAll(circleService.update(circleId, getJsonFromMessage(message), getMustLoginUserFromShiro(), request));
//		return message.getMap();
//	}
	
	/**
	 * 删除任务
	 * @return
	 */
	@RequestMapping(value = "/{clockId}", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> delete(Model model, HttpServletRequest request, @PathVariable("clockId") int clockId){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(clockService.delete(clockId, getMustLoginUserFromShiro(), request));
		return message.getMap();
	}
	
	/**
	 * 获取指定日期的打卡任务的列表
	 * @return
	 */
	@RequestMapping(value = "/date/{date}/clocks", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> dateClocks(@PathVariable("date")String date, Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(clockService.dateClocks(date, getMustLoginUserFromShiro(), request));
		
		return message.getMap();
	}
	
	/**
	 * 获取自己在进行中的列表
	 * @return
	 */
	@RequestMapping(value = "/ongoings", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> ongoings(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(clockService.getOngoingClocks(getMustLoginUserFromShiro(), getJsonFromMessage(message), request));
		return message.getMap();
	}
	
	/**
	 * 获取自己结束的列表
	 * @return
	 */
	@RequestMapping(value = "/endeds", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> endeds(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(clockService.getEndeds(getMustLoginUserFromShiro(), getJsonFromMessage(message), request));
		return message.getMap();
	}
	
	/**
	 * 获取系统默认任务的列表
	 * @return
	 */
	@RequestMapping(value = "/system/clocks", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> dateClocks(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(clockService.systemClocks(getMustLoginUserFromShiro(), request));
		return message.getMap();
	}
	
	/**
	 * 获取任务基本统计信息
	 * @param clockId
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/{clockId}/statistics", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> dynamics(@PathVariable("clockId") int clockId, Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(clockService.statistics(clockId, getJsonFromMessage(message), getMustLoginUserFromShiro(), request));
		return message.getMap();
	}
}
