package com.cn.leedane.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cn.leedane.model.TimeLineBean;
import com.cn.leedane.service.CircleOfFriendService;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.ResponseMap;

@RestController
@RequestMapping(value = ControllerBaseNameUtil.cof)
public class CircleOfFriendController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());
	//朋友圈service
	@Autowired
	private CircleOfFriendService<TimeLineBean> circleOfFriendService;
	
	/**
	 * 获取朋友圈列表
	 * @return
	 */
	@RequestMapping(value = "/circleOfFriends", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> paging(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(request);
		message.putAll(circleOfFriendService.getLimit(getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
	
	/**
	 * 分页获取朋友圈列表
	 * @return
	 */
	@RequestMapping(value = "/circleOfFriends/paging", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> paging(
			@RequestParam(value = "page_size") Integer pageSize, 
			@RequestParam("current") Integer current, 
			@RequestParam("total") Integer total, 
			HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(request);
		message.putAll(circleOfFriendService.paging(pageSize, current, total, getUserFromMessage(message), request));
		return message.getMap();
	}
	
}
