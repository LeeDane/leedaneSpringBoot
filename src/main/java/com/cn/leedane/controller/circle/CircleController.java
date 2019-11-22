package com.cn.leedane.controller.circle;

import com.cn.leedane.controller.BaseController;
import com.cn.leedane.model.circle.CircleBean;
import com.cn.leedane.model.circle.CircleSettingBean;
import com.cn.leedane.service.circle.CircleService;
import com.cn.leedane.service.circle.CircleSettingService;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.JsonUtil;
import com.cn.leedane.utils.ResponseMap;
import net.sf.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 圈子接口controller
 * @author LeeDane
 * 2017年6月11日 下午4:21:49
 * version 1.0
 */
@RestController
@RequestMapping(value = ControllerBaseNameUtil.cc)
public class CircleController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());

	@Autowired
	private CircleService<CircleBean> circleService;
	
	@Autowired
	private CircleSettingService<CircleSettingBean> circleSettingService;
		
	/**
	 * 检查是否能创建圈子
	 * @return
	 */
	@RequestMapping(value = "/check", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> check(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
//		int i = 10/0;
		checkRoleOrPermission(model, request);
		message.putAll(circleService.check(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 创建圈子
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> add(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(circleService.create(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 修改圈子
	 * @return
	 */
	@RequestMapping(value = "/{circleId}", method = RequestMethod.PUT, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> update(@PathVariable("circleId") long circleId, Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(circleService.update(circleId, getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 获取圈子分页列表
	 * @return
	 */
	@RequestMapping(value = "/circles", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> paging(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(circleService.paging(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 删除圈子
	 * @return
	 */
	@RequestMapping(value = "/{circleId}", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> delete(Model model, HttpServletRequest request, @PathVariable("circleId") long circleId){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(circleService.delete(circleId, getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 申请加入圈子之前判断是否可以加入
	 * @return
	 */
	@RequestMapping(value = "/{circleId}/join/check", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> joinCheck(@PathVariable("circleId") long circleId, Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(circleService.joinCheck(circleId, getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 申请加入圈子
	 * @return
	 */
	@RequestMapping(value = "/{circleId}/join", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> join(@PathVariable("circleId") long circleId, Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(circleService.join(circleId, getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 离开圈子
	 * @return
	 */
	@RequestMapping(value = "/{circleId}/leave", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> leave(@PathVariable("circleId") long circleId, Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(circleService.leave(circleId, getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 分页获取权限列表
	 * @return
	 */
	@RequestMapping(value = "/{cid}/admins", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> roles(Model model, HttpServletRequest request, @PathVariable("cid") long cid){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(circleService.admins(cid, getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 角色的分配
	 * @return
	 */
	@RequestMapping(value = "/{cid}/admins/allot", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> allot(Model model, HttpServletRequest request, @PathVariable("cid") long cid){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		JSONObject json = getJsonFromMessage(message);
		String admins = JsonUtil.getStringValue(json, "admins");
		
		message.putAll(circleService.allot(cid, admins, getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 圈子首页的一些初始化参数获取
	 * @return
	 */
	@RequestMapping(value = "/{cid}/init", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> initialize(Model model, HttpServletRequest request, @PathVariable("cid") long cid){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(circleService.initialize(cid, getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	
	/**
	 * 圈子首页的初始化数据(app客户端使用)
	 * @return
	 */
	@RequestMapping(value = "/init", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> init(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(circleService.init(getUserFromMessage(message), getHttpRequestInfo(request)));
		message.put("isSuccess", true);
		System.out.println(JSONObject.fromObject(message.getMap()).toString());
		return message.getMap();
	}
}
