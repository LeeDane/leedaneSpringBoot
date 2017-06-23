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
import com.cn.leedane.model.circle.CirclePostBean;
import com.cn.leedane.service.circle.CirclePostService;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.ResponseMap;

/**
 * 帖子接口controller
 * @author LeeDane
 * 2017年6月20日 下午6:07:49
 * version 1.0
 */
@RestController
@RequestMapping(value = ControllerBaseNameUtil.cc)
public class CirclePostController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());

	@Autowired
	private CirclePostService<CirclePostBean> circlePostService;
	
	/**
	 * 写帖子
	 * @return
	 */
	@RequestMapping(value = "/{circleId}/post", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> add(@PathVariable("circleId") int circleId, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(request);
		message.putAll(circlePostService.add(circleId, getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
	
	/**
	 * 修改帖子
	 * @return
	 */
	@RequestMapping(value = "/{circleId}/post", method = RequestMethod.PUT, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> update(@PathVariable("circleId") int circleId, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(request);
		message.putAll(circlePostService.update(circleId, getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
	
	/**
	 * 删除帖子
	 * @return
	 */
	@RequestMapping(value = "/{circleId}/post/{postId}", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> delete(@PathVariable("circleId") int circleId, @PathVariable("postId") int postId, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(request);
		message.putAll(circlePostService.delete(circleId, postId, getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
		
	/**
	 * 获取圈子帖子分页列表
	 * @return
	 */
	@RequestMapping(value = "/{circleId}/posts", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> paging(@PathVariable("circleId") int circleId, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(request);
		message.putAll(circlePostService.paging(circleId, getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
	
	/**
	 * 帖子评论
	 * @return
	 */
	@RequestMapping(value = "/{circleId}/post/{postId}/comment", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> comment(@PathVariable("circleId") int circleId, @PathVariable("postId") int postId, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(request);
		message.putAll(circlePostService.comment(circleId, postId, getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
	
	/**
	 * 帖子转发
	 * @return
	 */
	@RequestMapping(value = "/{circleId}/post/{postId}/transmit", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> transmit(@PathVariable("circleId") int circleId, @PathVariable("postId") int postId, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(request);
		message.putAll(circlePostService.transmit(circleId, postId, getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
	
	/**
	 * 帖子点赞
	 * @return
	 */
	@RequestMapping(value = "/{circleId}/post/{postId}/zan", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> zan(@PathVariable("circleId") int circleId, @PathVariable("postId") int postId, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(request);
		message.putAll(circlePostService.zan(circleId, postId, getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
}
