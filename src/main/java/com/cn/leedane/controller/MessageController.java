package com.cn.leedane.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cn.leedane.utils.EnumUtil;

@Controller
@RequestMapping("/leedane/message")
public class MessageController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());

	/**
	 * 执行发送消息的方法
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/send")
	public String send(HttpServletRequest request, HttpServletResponse response){
		Map<String, Object> message = new HashMap<String, Object>();
		long start = System.currentTimeMillis();
		try {
			if(!checkParams(message, request)){
				printWriter(message, response, start);
				return null;
			}
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
			printWriter(message, response, start);
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		printWriter(message, response, start);
		return null;
		
	}
	
	/**
	 * 执行发送消息的方法
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/receive")
	public String receive(HttpServletRequest request, HttpServletResponse response){
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
		Map<String, Object> message = new HashMap<String, Object>();
		long start = System.currentTimeMillis();
		message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		printWriter(message, response, start);
		return null;
	}
}
