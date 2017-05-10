package com.cn.leedane.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cn.leedane.model.ZanBean;
import com.cn.leedane.service.ZanService;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.ResponseMap;

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
	public Map<String, Object> add(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(request);
		message.putAll(zanService.addZan(getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
	
	/**
	 * 取消赞
	 * @return
	 */
	@RequestMapping(value = "/zan", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> delete(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(request);
		message.putAll(zanService.deleteZan(getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
	
	/**
	 * 获取赞列表
	 * @return
	 */
	@RequestMapping(value = "/zans", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> paging(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(request);
		List<Map<String, Object>> result= zanService.getLimit(getJsonFromMessage(message), getUserFromMessage(message), request);
		logger.info("获得赞的数量：" +result.size());
		message.put("isSuccess", true);
		message.put("message", result);
		return message.getMap();
	}
	
	/**
	 * 获取点赞用户列表
	 * @return
	 */
	@RequestMapping(value = "/allZanUsers", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> getAllZanUser(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(request);
		message.putAll(zanService.getAllZanUser(getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
}
