package com.cn.leedane.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.annotations.Param;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cn.leedane.model.JobManageBean;
import com.cn.leedane.service.JobManageService;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.ResponseMap;

@RestController
@RequestMapping(value = ControllerBaseNameUtil.jm)
public class JobManageController extends BaseController{
	
	@Autowired
	private JobManageService<JobManageBean> jobManageService;
	
	/**
	 * 添加任务
	 * @return
	 * @throws SchedulerException 
	 */
	@RequestMapping(value = "/job", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> add(Model model, HttpServletRequest request) throws SchedulerException{
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(jobManageService.add(getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
	
	/**
	 * 修改任务
	 * @return
	 * @throws SchedulerException 
	 */
	@RequestMapping(value = "/job", method = RequestMethod.PUT, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> update(Model model, HttpServletRequest request) throws SchedulerException{
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(jobManageService.update(getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
	
	/**
	 * 删除任务
	 * @return
	 * @throws SchedulerException 
	 */
	@RequestMapping(value = "/job/{jid}", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> delete(@PathVariable("jid") int jid, Model model, HttpServletRequest request) throws SchedulerException{
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(jobManageService.delete(jid, request));
		return message.getMap();
	}
	
	/**
	 * 分页获取任务列表
	 * @return
	 */
	@RequestMapping(value = "/jobs", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> paging(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(jobManageService.paging(getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
	
	/**
	 * 批量删除任务
	 * @return
	 * @throws SchedulerException 
	 */
	@RequestMapping(value = "/jobs", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> deletes(Model model, HttpServletRequest request, @Param("jobids") String jobids) throws SchedulerException{
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(jobManageService.deletes(jobids, getUserFromMessage(message), request));
		return message.getMap();
	}
}
