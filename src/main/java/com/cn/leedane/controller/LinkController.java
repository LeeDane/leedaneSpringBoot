package com.cn.leedane.controller;

import com.cn.leedane.model.LinkManageBean;
import com.cn.leedane.service.LinkManageService;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.JsonUtil;
import com.cn.leedane.utils.ResponseMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 链接管理接口controller
 * @author LeeDane
 * 2017年4月20日 下午4:59:32
 * Version 1.0
 */
@RestController
@RequestMapping(value = ControllerBaseNameUtil.ln)
public class LinkController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());

	@Autowired
	private LinkManageService<LinkManageBean> linkManageService;
		
	/**
	 * 添加权限
	 * @return
	 */
	@RequestMapping(value = "/link", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> add(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(linkManageService.save(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 修改权限
	 * @return
	 */
	@RequestMapping(value = "/link", method = RequestMethod.PUT, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> update(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(linkManageService.edit(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 删除权限
	 * @return
	 */
	@RequestMapping(value = "/link/{lnid}", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> delete(Model model, HttpServletRequest request, @PathVariable("lnid") int lnid){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(linkManageService.delete(lnid, getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 分页获取权限列表
	 * @return
	 */
	@RequestMapping(value = "/links", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> paging(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(linkManageService.paging(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 批量删除权限
	 * @return
	 */
	@RequestMapping(value = "/links", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> deletes(Model model, HttpServletRequest request, @Param("lnids") String lnids){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(linkManageService.deletes(lnids, getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 获取全部权限或者角色列表
	 * @return
	 */
	@RequestMapping(value = "/link/{lnid}/roleOrPermissions", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> roleOrPermissions(Model model, HttpServletRequest request, @PathVariable("lnid") int lnid){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		boolean role = JsonUtil.getBooleanValue(getJsonFromMessage(message), "role");
		message.putAll(linkManageService.roleOrPermissions(lnid, role, getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 角色、权限的分配
	 * @return
	 */
	@RequestMapping(value = "/link/{lnid}/roleOrPermissions/allot", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> allot(Model model, HttpServletRequest request, @PathVariable("lnid") int lnid){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(linkManageService.allot(lnid, getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
}
