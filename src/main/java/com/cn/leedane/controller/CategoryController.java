package com.cn.leedane.controller;

import com.cn.leedane.model.CategoryBean;
import com.cn.leedane.service.CategoryService;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.JsonUtil;
import com.cn.leedane.utils.ResponseMap;
import com.cn.leedane.utils.ResponseModel;
import net.sf.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 分类管理的controller类
 * @author LeeDane
 * 2017年6月28日 下午4:49:49
 * version 1.0
 */
@RestController
@RequestMapping(value = ControllerBaseNameUtil.cg)
public class CategoryController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());

	@Autowired
	private CategoryService<CategoryBean> categoryService;
	/**
	 * 添加节点
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/category", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public ResponseModel add(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getModel();
		JSONObject params = getJsonFromMessage(message);
		long pid = JsonUtil.getLongValue(params, "pid", 0);
		if(pid < 1)
			//必须是管理员权限才能操作此接口
			checkAnyRoleAuthor(RoleController.ADMIN_ROLE_CODE);
		checkRoleOrPermission(model, request);
		return categoryService.add(params, getUserFromMessage(message), getHttpRequestInfo(request));
	}
	
	/**
	 * 获取分类的直接一级子节点
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/category/{pid}/children", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public ResponseModel children(@PathVariable("pid") long pid, Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getModel();
		checkRoleOrPermission(model, request);
		return categoryService.children(SecurityUtils.getSubject().hasRole(RoleController.ADMIN_ROLE_CODE), pid, getUserFromMessage(message), getHttpRequestInfo(request));
	}
	
	/**
	 * 修改节点
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/category/{cid}", method = RequestMethod.PUT, produces = {"application/json;charset=UTF-8"})
	public ResponseModel update(@PathVariable("cid") long cid, Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getModel();
		checkRoleOrPermission(model, request);
		return categoryService.update(SecurityUtils.getSubject().hasRole(RoleController.ADMIN_ROLE_CODE), cid, getJsonFromMessage(message) , getUserFromMessage(message), getHttpRequestInfo(request));
	}
	
	/**
	 * 删除节点
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/category/{cid}", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"})
	public ResponseModel delete(@PathVariable("cid") long cid, Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getModel();
		checkRoleOrPermission(model, request);
		return categoryService.delete(SecurityUtils.getSubject().hasRole(RoleController.ADMIN_ROLE_CODE), cid, getUserFromMessage(message), getHttpRequestInfo(request));
	}
}
