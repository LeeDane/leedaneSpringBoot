package com.cn.leedane.controller.circle;

import com.cn.leedane.controller.BaseController;
import com.cn.leedane.model.circle.CirclePostBean;
import com.cn.leedane.service.circle.CirclePostService;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.ResponseMap;
import com.cn.leedane.utils.ResponseModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

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
	public ResponseModel add(@PathVariable("circleId") long circleId, Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getModel();
		checkRoleOrPermission(model, request);
		return circlePostService.add(circleId, getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request));
	}
	
	/**
	 * 修改帖子
	 * @return
	 */
	@RequestMapping(value = "/{circleId}/post", method = RequestMethod.PUT, produces = {"application/json;charset=UTF-8"})
	public ResponseModel update(@PathVariable("circleId") long circleId, Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getModel();
		checkRoleOrPermission(model, request);
		return circlePostService.update(circleId, getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request));
	}
	
	/**
	 * 删除帖子
	 * @return
	 */
	@RequestMapping(value = "/{circleId}/post/{postId}", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"})
	public ResponseModel delete(@PathVariable("circleId") long circleId, @PathVariable("postId") long postId, Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getModel();
		checkRoleOrPermission(model, request);
		return circlePostService.delete(circleId, postId, getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request));
	}
		
	/**
	 * 获取圈子帖子分页列表
	 * @return
	 */
	@RequestMapping(value = "/{circleId}/posts", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public ResponseModel paging(@PathVariable("circleId") long circleId, Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getModel();
		checkRoleOrPermission(model, request);
		return circlePostService.paging(circleId, getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request));
	}
	
	/**
	 * 帖子评论
	 * @return
	 */
	@RequestMapping(value = "/{circleId}/post/{postId}/comment", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public ResponseModel comment(@PathVariable("circleId") long circleId, @PathVariable("postId") long postId, Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getModel();
		checkRoleOrPermission(model, request);
		return circlePostService.comment(circleId, postId, getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request));
	}
	
	/**
	 * 帖子转发
	 * @return
	 */
	@RequestMapping(value = "/{circleId}/post/{postId}/transmit", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public ResponseModel transmit(@PathVariable("circleId") long circleId, @PathVariable("postId") long postId, Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getModel();
		checkRoleOrPermission(model, request);
		return circlePostService.transmit(circleId, postId, getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request));
	}
	
	/**
	 * 帖子点赞
	 * @return
	 */
	@RequestMapping(value = "/{circleId}/post/{postId}/zan", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public ResponseModel zan(@PathVariable("circleId") long circleId, @PathVariable("postId") long postId, Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getModel();
		checkRoleOrPermission(model, request);
		return circlePostService.zan(circleId, postId, getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request));
	}
	
	/**
	 * 等待审核帖子总数
	 * @return
	 */
	@RequestMapping(value = "/{circleId}/post/nochecktotal", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public ResponseModel noCheckTotal(@PathVariable("circleId") long circleId, Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getModel();
		checkRoleOrPermission(model, request);
		return circlePostService.noCheckTotal(circleId, getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request));
	}
	
	/**
	 * 等待审核帖子列表
	 * @return
	 */
	@RequestMapping(value = "/{circleId}/post/nochecks", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public ResponseModel noCheckList(@PathVariable("circleId") long circleId, Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getModel();
		checkRoleOrPermission(model, request);
		return circlePostService.noCheckList(circleId, getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request));
	}
	
	/**
	 * 审核帖子
	 * @return
	 */
	@RequestMapping(value = "/{circleId}/post/{postId}/check", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public ResponseModel check(@PathVariable("circleId") long circleId, @PathVariable("postId") long postId, Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getModel();
		checkRoleOrPermission(model, request);
		return circlePostService.check(circleId, postId, getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request));
	}
	
}
