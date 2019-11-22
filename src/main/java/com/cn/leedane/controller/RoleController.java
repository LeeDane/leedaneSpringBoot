package com.cn.leedane.controller;

import com.cn.leedane.model.RoleBean;
import com.cn.leedane.service.RoleService;
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
 * 角色管理接口controller
 * @author LeeDane
 * 2017年4月20日 上午9:52:07
 * Version 1.0
 */
@RestController
@RequestMapping(value = ControllerBaseNameUtil.rl)
public class RoleController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());

	@Autowired
	private RoleService<RoleBean> roleService;
	
	/**
	 * 系统管理员固定角色编码
	 */
	public static final String ADMIN_ROLE_CODE = "ADMIN_MANAGER";
	
	/**
	 * 商城管理员固定角色编码
	 */
	public static final String MALL_ROLE_CODE = "MALL_MANAGER";
	
	/**
	 *股票模块固定角色编码
	 */
	public static final String STOCK_ROLE_CODE = "STOCK_ROLE";

	/**
	 *圈子模块固定角色编码
	 */
	public static final String CIRCLE_ROLE_CODE = "CIRCLE_ROLE";

	/**
	 *购物模块固定角色编码
	 */
	public static final String SHOPPING_ROLE_CODE = "SHOPPING_ROLE";


	/**
	 *宝宝模块固定角色编码
	 */
	public static final String BABY_ROLE_CODE = "BABY_ROLE";
	
	/**
	 * 添加权限
	 * @return
	 */
	@RequestMapping(value = "/role", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> add(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(roleService.save(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 修改权限
	 * @return
	 */
	@RequestMapping(value = "/role", method = RequestMethod.PUT, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> update(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(roleService.edit(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 删除权限
	 * @return
	 */
	@RequestMapping(value = "/role/{rlid}", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> delete(Model model, HttpServletRequest request, @PathVariable("rlid") long rlid){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(roleService.delete(rlid, getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 分页获取权限列表
	 * @return
	 */
	@RequestMapping(value = "/roles", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> paging(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(roleService.paging(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 批量删除权限
	 * @return
	 */
	@RequestMapping(value = "/roles", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> deletes(Model model, HttpServletRequest request, @Param("rlids") String rlids){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(roleService.deletes(rlids, getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 获取全部用户列表
	 * @return
	 */
	@RequestMapping(value = "/role/{rlid}/users", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> roles(Model model, HttpServletRequest request, @PathVariable("rlid") long rlid){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(roleService.users(rlid, getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 角色的分配
	 * @return
	 */
	@RequestMapping(value = "/role/{rlid}/users/allot", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> allot(Model model, HttpServletRequest request, @PathVariable("rlid") long rlid){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		JSONObject json = getJsonFromMessage(message);
		String users = JsonUtil.getStringValue(json, "users");
		
		message.putAll(roleService.allot(rlid, users, getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
}
