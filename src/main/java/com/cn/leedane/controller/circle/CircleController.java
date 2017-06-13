package com.cn.leedane.controller.circle;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cn.leedane.controller.BaseController;
import com.cn.leedane.model.circle.CircleBean;
import com.cn.leedane.service.circle.CircleService;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.ResponseMap;

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
		
	/**
	 * 检查是否能创建圈子
	 * @return
	 */
	@RequestMapping(value = "/check", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> check(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(request);
		message.putAll(circleService.check(getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
	
	/**
	 * 创建圈子
	 * @return
	 */
	@RequestMapping(value = "/circle", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> add(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(request);
		message.putAll(circleService.create(getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
	
	/**
	 * 修改圈子
	 * @return
	 */
	@RequestMapping(value = "/circle", method = RequestMethod.PUT, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> update(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(request);
		message.putAll(circleService.update(getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
	
	/**
	 * 获取圈子分页列表
	 * @return
	 */
	@RequestMapping(value = "/circles", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> paging(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(request);
		message.putAll(circleService.paging(getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
	
	/**
	 * 删除圈子
	 * @return
	 */
	@RequestMapping(value = "/circle/{cid}", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> delete(HttpServletRequest request, @PathVariable("cid") int cid){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(request);
		message.putAll(circleService.delete(cid, getUserFromMessage(message), request));
		return message.getMap();
	}
	
}
