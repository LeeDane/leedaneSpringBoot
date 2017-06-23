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
		/*String ids = "6565, 6566, 6571, 6572, 6573, 6574, 6575, 9249, 9250, 9252, 9424, 9437, 9442, 9444, 9446, 9487, 9489, 9490, 9492, 9494, 9496, 9499, 9501, 9502, 9503, 9505, 9506, 9507, 9509, 9510, 9511, 9513, 9514, 9519, 9520, 9524, 9527, 9532, 9533, 9535, 9536, 9540, 9542, 9546, 9607, 9744, 9746, 9748, 9762, 9764, 9766, 9768, 9775, 9779, 9806, 9810, 9812, 9828, 9832, 9845, 9847, 9850, 9856, 9857, 9859, 9861, 9862, 9864, 9865, 9868, 9870, 9872, 9874, 9876, 9879,9880, 9887, 9889, 9892, 9894, 9899, 9901, 9902, 9904, 9905, 9907, 9914, 9917, 9919, 9921, 9929, 9933, 9945, 9949, 9951, 9958,9960, 9967, 9971, 9975, 9977, 9979, 9981, 9986, 9993, 9995, 9999, 10001, 10003, 10005, 10007, 10011, 10012, 10015, 10018, 10023, 10027, 10029, 10032, 10033, 10034, 10036, 10038, 10040, 10042, 10043, 9522";
		String[] idArray = ids.split(",");
		List<String> list = new ArrayList<String>();
		for(String id: idArray){
			int d = StringUtil.changeObjectToInt(id.trim());
			list.add(String.valueOf(d));
		}
		BlogSolrHandler.getInstance().deleteBeans(list);*/
		
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
