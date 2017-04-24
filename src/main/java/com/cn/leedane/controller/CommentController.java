package com.cn.leedane.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cn.leedane.model.CommentBean;
import com.cn.leedane.service.CommentService;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.ResponseMap;

@RestController
@RequestMapping(value = ControllerBaseNameUtil.cm)
public class CommentController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());

	//评论service
	@Autowired
	private CommentService<CommentBean> commentService;
	
	/**
	 * 发表评论
	 * @return
	 */
	@RequestMapping(value = "/comment", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> add(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(request);
		message.putAll(commentService.add(getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}

	/**
	 * 获取对象的主要评论列表
	 * @return
	 */
	@RequestMapping(value = "/comments", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> paging(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		
		message.putAll(commentService.getCommentsByLimit(getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
	
	
	/**
	 * 获取每条评论的子评论列表
	 * @return
	 */
	@RequestMapping(value = "/items", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> getItemsPaging(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(request);
		message.putAll(commentService.getOneCommentItemsByLimit(getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
	
	/**
	 * 获取每一条评论的评论总数
	 * @return
	 */
	@RequestMapping(value = "/count", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> getCountByObject(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(request);
		message.putAll(commentService.getCountByObject(getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
	
	/**
	 * 获取用户所有的评论数量
	 * @return
	 */
	@RequestMapping(value = "/countByUser", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> getCommentsCountByUser(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(request);
		message.putAll(commentService.getCountByUser(getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
	
	/**
	 * 删除评论
	 * @return
	 */
	@RequestMapping(value = "/comment", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> delete(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(request);
		message.putAll(commentService.deleteComment(getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
	
	/**
	 * 更改评论编辑状态
	 * {'can_comment':true, 'table_name':'t_mood', 'table_id': 1},所有参数全部必须
	 * @return
	 */
	@RequestMapping(value = "/comment", method = RequestMethod.PUT, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> updateCommentStatus(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(request);
		message.putAll(commentService.updateCommentStatus(getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
	
	/**
	 * 获取留言板列表
	 * {'can_comment':true, 'table_name':'t_message_board_1', 'table_id': 1},所有参数全部必须
	 * @return
	 */
	@RequestMapping(value = "/user/{uid}/messageBoards", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> messageBoards(HttpServletRequest request, @PathVariable("uid") int uid){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(request);
		message.putAll(commentService.getMessageBoards(uid, getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
}
