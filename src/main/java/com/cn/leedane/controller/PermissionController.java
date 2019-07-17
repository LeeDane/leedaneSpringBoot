package com.cn.leedane.controller;

import com.cn.leedane.model.PermissionBean;
import com.cn.leedane.service.PermissionService;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.JsonUtil;
import com.cn.leedane.utils.ResponseMap;
import net.sf.json.JSONObject;
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
 * 权限管理接口controller
 * @author LeeDane
 * 2017年4月17日 下午2:15:23
 * version 1.0
 */
@RestController
@RequestMapping(value = ControllerBaseNameUtil.pm)
public class PermissionController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());

	@Autowired
	private PermissionService<PermissionBean> permissionService;
	
	/**
	 * 添加权限
	 * @return
	 */
	@RequestMapping(value = "/permission", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> add(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(permissionService.save(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 修改权限
	 * @return
	 */
	@RequestMapping(value = "/permission", method = RequestMethod.PUT, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> update(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(permissionService.edit(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 删除权限
	 * @return
	 */
	@RequestMapping(value = "/permission/{pmid}", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> delete(Model model, HttpServletRequest request, @PathVariable("pmid") int pmid){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(permissionService.delete(pmid, getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 分页获取权限列表
	 * @return
	 */
	@RequestMapping(value = "/permissions", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> paging(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(permissionService.paging(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 批量删除权限
	 * @return
	 */
	@RequestMapping(value = "/permissions", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> deletes(Model model, HttpServletRequest request, @Param("pmids") String pmids){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(permissionService.deletes(pmids, getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 分页获取权限列表
	 * @return
	 */
	@RequestMapping(value = "/permission/{pmid}/roles", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> roles(Model model, HttpServletRequest request, @PathVariable("pmid") int pmid){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(permissionService.roles(pmid, getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 角色的分配
	 * @return
	 */
	@RequestMapping(value = "/permission/{pmid}/roles/allot", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> allot(Model model, HttpServletRequest request, @PathVariable("pmid") int pmid){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		JSONObject json = getJsonFromMessage(message);
		String roles = JsonUtil.getStringValue(json, "roles");
		
		message.putAll(permissionService.allot(pmid, roles, getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
}
