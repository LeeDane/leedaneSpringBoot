package com.cn.leedane.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.ResponseMap;

@RestController
@RequestMapping(value = ControllerBaseNameUtil.ms)
public class MessageController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());

	/**
	 * 执行发送消息的方法
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/message", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> send(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		try {
			if(!checkParams(message, request))
				return message.getMap();
			
			/*UserBean user = (UserBean) getSession().get(ConstantsUtil.USER_SESSION);
			int fromUserID = 1;
			if(user != null){
				fromUserID = user.getId();
			}
			 
			int toUserID = jo.getInt("uid");
			String msg = jo.getString("content");
			SendAndRecieveObject object = new SendAndRecieveObject();
			object.setToUserID(String.valueOf(toUserID));
			object.setFromUserID(String.valueOf(fromUserID));
			object.setCreateTime(new Date());
			object.setMsg(msg);
			SendMessage sender = new SendMessage(object);
			sender.sendMsg();
			message.put("isSuccess", true);
			message.put("message", "发送消息成功");*/
			return message.getMap();
		} catch (Exception e) {
			e.printStackTrace();
		}
		message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		return message.getMap();
		
	}
	
	/**
	 * 执行发送消息的方法
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/message/receive", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> receive(HttpServletRequest request){
		/*UserBean user = (UserBean) session.get(ConstantsUtil.USER_SESSION);
		if(user != null){
			RecieveMessage recieveMessage = new RecieveMessage(String.valueOf(user.getId()));
			String getMsg = recieveMessage.getMsg();
			if(!StringUtil.isNull(getMsg)){
				String array[] = getMsg.split("@@");
				if(array.length == 3){
					SendAndRecieveObject object = new SendAndRecieveObject();
					object.setFromUserID(array[0]);
					object.setMsg(array[1]);
					object.setCreateTime(DateUtil.stringToDate(array[2]));
					message.put("isSuccess", true);
					message.put("message", object);
					return SUCCESS;
				}
			}
			message.put("isSuccess", false);
			message.put("message", "获取信息出错");
		}else{
			message.put("isSuccess", false);
			message.put("message", "请先登录");
		}*/
		ResponseMap message = new ResponseMap();
		message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		return message.getMap();
	}
}
