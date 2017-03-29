package com.cn.leedane.controller;

import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cn.leedane.model.FriendBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.service.FriendService;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.ResponseMap;

@RestController
@RequestMapping(value = ControllerBaseNameUtil.fr)
public class FriendController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());
	
	@Autowired
	private FriendService<FriendBean> friendService;

	// 操作日志
	@Autowired
	protected OperateLogService<OperateLogBean> operateLogService;
	
	/**
	 * 解除好友关系
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/friend", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> delete(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		try {
			if(!checkParams(message, request))
				return message.getMap();
			
			message.putAll(friendService.deleteFriends(getJsonFromMessage(message), getUserFromMessage(message), request));
			return message.getMap();
		} catch (Exception e) {
			e.printStackTrace();
		}
		message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		return message.getMap();
	}
	/**
	 * 发起增加好友
	 * @return
	 */
	@RequestMapping(value = "/friend", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> add(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		try {
			//{"id":1, "to_user_id": 2, "add_introduce":"你好,我是XX"}
			if(!checkParams(message, request))
				return message.getMap();
			
			message.putAll(friendService.addFriend(getJsonFromMessage(message), getUserFromMessage(message), request));
			return message.getMap();
		} catch (Exception e) {
			//resmessage = "抱歉，添加好友执行出现异常！请核实提交的信息后重试或者联系管理员";
			e.printStackTrace();
		}
		message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		return message.getMap();
	}
	
	/**
	 * 同意增加好友
	 * @return
	 */
	@RequestMapping(value = "/friend/agree", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> agreeFriend(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		try {
			//{"relation_id":100}
			if(!checkParams(message, request))
				return message.getMap();
			
			message.putAll(friendService.addAgree(getJsonFromMessage(message), getUserFromMessage(message), request));
			return message.getMap();
		} catch (Exception e) {
			e.printStackTrace();
		}
		message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);      
		return message.getMap();
	}
	
	/**
	 * 判读两人是否是好友
	 * @return
	 */
	@RequestMapping(value = "/is", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> isFriend(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		try {
			//{"id":1, "to_user_id": 2}
			if(!checkParams(message, request))
				return message.getMap();
			
			JSONObject json = getJsonFromMessage(message);
			UserBean user = getUserFromMessage(message);
			if(json.has("to_user_id")) {
				int to_user_id = json.getInt("to_user_id");			
				UserBean toUser = userService.findById(to_user_id);
				if(toUser!= null){
					message.put("isSuccess", friendService.isFriend(getUserFromMessage(message).getId(), to_user_id));
					
					// 保存操作日志信息
					String subject = user.getAccount()+"判断跟"+toUser.getAccount()+"是否是朋友";
					this.operateLogService.saveOperateLog(user, request, new Date(), subject, "isFriend", 1, 0);
					return message.getMap();
				}	
					
			}
		} catch (Exception e) {
			e.printStackTrace();
		}     
        message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		return message.getMap();
	}
	
	/**
	 * 获取已经跟我成为好友关系的分页列表
	 * @return
	 */
	@RequestMapping(value = "/friends", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> friendsPaging(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		try {
			//{"id":1, "to_user_id": 2}
			if(!checkParams(message, request))
				return message.getMap();
			
			message.putAll(friendService.friendsAlreadyPaging(getJsonFromMessage(message), getUserFromMessage(message), request));
			return message.getMap();
		} catch (Exception e) {
			e.printStackTrace();
		}     
        message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		return message.getMap();
	}
	
	/**
	 * 获取还未跟我成为好友关系的用户(我发起对方未答应或者对方发起我还未答应的)
	 * @return
	 */
	@RequestMapping(value = "/notyetfriends", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> friendsNotyetPaging(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		try {
			//{"id":1, "to_user_id": 2}
			if(!checkParams(message, request))
				return message.getMap();
			
			message.putAll(friendService.friendsNotyetPaging(getJsonFromMessage(message), getUserFromMessage(message), request));
			return message.getMap();
		} catch (Exception e) {
			e.printStackTrace();
		}     
        message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		return message.getMap();
	}
	
	
	/**
	 * 获取全部已经跟我成为好友关系列表
	 * @return
	 */
	@RequestMapping(value = "/friends/all", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> friends(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		try {
			if(!checkParams(message, request))
				return message.getMap();
			
			message.putAll(friendService.friends(getJsonFromMessage(message), getUserFromMessage(message), request));
			return message.getMap();
		} catch (Exception e) {
			e.printStackTrace();
		}     
        message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		return message.getMap();
	}
	
	/**
	 * 获取我发送的好友请求列表
	 * @return
	 */
	@RequestMapping(value = "/friends/request", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> requestPaging(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		try {
			//{"id":1, "to_user_id": 2}
			if(!checkParams(message, request))
				return message.getMap();
			
			message.putAll(friendService.requestPaging(getJsonFromMessage(message), getUserFromMessage(message), request));
			return message.getMap();
		} catch (Exception e) {
			e.printStackTrace();
		}     
        message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		return message.getMap();
	}
	
	/**
	 * 获取等待我同意的好友关系列表
	 * @return
	 */
	@RequestMapping(value = "/friends/response", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> responsePaging(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		try {
			//{"id":1, "to_user_id": 2}
			if(!checkParams(message, request))
				return message.getMap();
			
			message.putAll(friendService.responsePaging(getJsonFromMessage(message), getUserFromMessage(message), request));
			return message.getMap();
		} catch (Exception e) {
			e.printStackTrace();
		}     
        message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		return message.getMap();
	}
	
	/**
	 * 用户本地联系人跟服务器上的好友进行匹配后返回
	 * @return
	 */
	@RequestMapping(value = "/friends/match", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> matchContact(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		try {
			//{"id":1, "to_user_id": 2}
			if(!checkParams(message, request))
				return message.getMap();
			
			message.putAll(friendService.matchContact(getJsonFromMessage(message), getUserFromMessage(message), request));
			return message.getMap();
		} catch (Exception e) {
			e.printStackTrace();
		}     
        message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		return message.getMap();
	}
	
	/**
	 * 话题列表
	 * @return
	 */
	@RequestMapping(value = "/topic", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> topic(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		try {
			//{"id":1, "to_user_id": 2}
			if(!checkParams(message, request))
				return message.getMap();
			
			message.putAll(friendService.matchContact(getJsonFromMessage(message), getUserFromMessage(message), request));
			return message.getMap();
		} catch (Exception e) {
			e.printStackTrace();
		}     
        message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		return message.getMap();
	}
}
