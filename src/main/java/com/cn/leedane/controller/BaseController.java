package com.cn.leedane.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.springframework.util.CollectionUtils;

import com.cn.leedane.handler.UserHandler;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.service.UserService;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.JsonUtil;
import com.cn.leedane.utils.SessionManagerUtil;
import com.cn.leedane.utils.StringUtil;

public class BaseController {
	
	@Resource
	protected UserService<UserBean> userService;
	
	@Resource
	protected UserHandler userHandler;
	
	/**
	 * 通过原先servlet方式输出json对象。
	 * 目的：解决复杂的文本中含有特殊的字符导致struts2的json
	 * 		解析失败，给客户端返回500的bug
	 */
	//请使用printWriter(Map<String, Object> message, HttpServletResponse response, long start)
	protected void printWriter(Map<String, Object> message, HttpServletResponse response){
		if(message.containsKey("json"))
			message.remove("json");
		
		if(message.containsKey("user"))
			message.remove("user");
		
		JSONObject jsonObject = JSONObject.fromObject(message);
		response.setCharacterEncoding("utf-8");
		System.out.println("服务器返回:"+jsonObject.toString());
		PrintWriter writer = null;
		try {
			writer = response.getWriter();
			writer.append(jsonObject.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(writer != null)
				writer.close();
		}
		
	}
	
	/**
	 * 通过原先servlet方式输出json对象。
	 * 目的：解决复杂的文本中含有特殊的字符导致struts2的json
	 * 		解析失败，给客户端返回500的bug
	 */
	protected void printWriter(Map<String, Object> message, HttpServletResponse response, long start){
		/*if(message.containsKey("json"))
			message.remove("json");
		
		if(message.containsKey("user"))
			message.remove("user");
		
		if(start > 0){
			long end = System.currentTimeMillis();
			message.put("consumeTime", (end - start) +"ms");
		}
		
		JSONObject jsonObject = JSONObject.fromObject(message);
		response.setCharacterEncoding("utf-8");
		System.out.println("服务器返回:"+jsonObject.toString());
		PrintWriter writer = null;
		try {
			writer = response.getWriter();
			writer.append(jsonObject.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(writer != null)
				writer.close();
		}*/
		
	}

	
	/**
	 * 校验请求参数
	 * @param request
	 * @return
	 */
	protected boolean checkParams(Map<String, Object> message, HttpServletRequest request){
		boolean result = false;
		try{
			return checkLogin(request, message);
			//UserBean user = (UserBean) request.getAttribute("user");
			//JSONObject json = JSONObject.fromObject(request.getAttribute("params"));
			//message.put("json", json);
			//message.put("user", user);
			/*if(message.containsKey("")){
				message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.请先登录.value));
				message.put("responseCode", EnumUtil.ResponseCode.请先登录.value);
			}*/
		}catch(Exception e){
			e.printStackTrace();
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.缺少请求参数.value));
			message.put("responseCode", EnumUtil.ResponseCode.缺少请求参数.value);
		}
		return result;
	}
	
	public boolean checkLogin(HttpServletRequest request, Map<String, Object> message){
		boolean result = false;
		Object sessionUserInfo = request.getSession().getAttribute(UserController.USER_INFO_KEY);
		
		UserBean user = null;
		//标记用户已经登录
		if(sessionUserInfo != null){
			result = true;
			user = (UserBean)sessionUserInfo;
			HttpSession session = SessionManagerUtil.getInstance().getSession(user.getId());
			if(session == null){
				user = null;
			}
		}
		
		//请求参数
		String params = request.getParameter("params");
		JSONObject json = null;
		if(StringUtil.isNull(params)){
			try {
				json = convertParameterMapToJsonObject(request.getParameterMap());
				System.out.println("请求参数:"+json.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			//校验用户信息
			json = JSONObject.fromObject(params);
		}
		
		if(json != null){
			message.put("json", json);
			//从请求ID获取用户信息
			if(user == null){
				if(json.has("id")){
					//设置为了防止过滤路径，直接在这里加载用户请求有id为默认登录用户
					try {
						user = userService.findById(JsonUtil.getIntValue(json, "id"));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			
			
			//从请求免登录码获取用户信息
			if(user == null && json.has("no_login_code") && json.has("account")){						
				//拿到免登陆码
				String noLoginCode = JsonUtil.getStringValue(json, "no_login_code");
				//拿到登录账户
				String account = JsonUtil.getStringValue(json, "account");
				user = userService.getUserByNoLoginCode(account, noLoginCode);
			}
			
			if(user != null){
				String returnErrorMeg = "";
				int returnErrorCode = 0;
				//获取登录用户的状态
				int status = user.getStatus();
				boolean canDo = false;
				
				//0:被禁止 1：正常，2、注册未激活  ，3：未完善信息 ， 4：被禁言 ，5:注销
				switch (status) {
					case ConstantsUtil.STATUS_DISABLE:
						returnErrorMeg = "账号"+user.getAccount()+"已经被禁用，有问题请联系管理员";
						returnErrorCode = EnumUtil.ResponseCode.账号已被禁用.value;
						break;
					case ConstantsUtil.STATUS_NORMAL:
						canDo = true;
						break;
					case ConstantsUtil.STATUS_NO_ACTIVATION:
						returnErrorMeg = "请先激活账号"+ user.getAccount();
						returnErrorCode = EnumUtil.ResponseCode.账号未被激活.value;
						break;
					case ConstantsUtil.STATUS_INFORMATION:
						returnErrorMeg = "请先完善账号"+ user.getAccount() +"的信息";
						returnErrorCode = EnumUtil.ResponseCode.请先完善账号信息.value;
						break;
					case ConstantsUtil.STATUS_NO_TALK:
						returnErrorMeg = "账号"+ user.getAccount()+"已经被禁言，有问题请联系管理员";
						returnErrorCode = EnumUtil.ResponseCode.账号已被禁言.value;
						break;
					case ConstantsUtil.STATUS_DELETE:
						returnErrorMeg = "账号"+ user.getAccount()+"已经被注销，有问题请联系管理员";
						returnErrorCode = EnumUtil.ResponseCode.账号已被注销.value;
						break;
					default:
						break;
				}
				
				userHandler.addLastRequestTime(user.getId());
				
				//当验证账号的状态是正常的情况，继续执行action
				if(canDo){
					message.put("user", user);
					result = true;
				}else{
					message.put("message", returnErrorMeg);
					message.put("responseCode", returnErrorCode);
				}
			}else{
				message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.请先登录.value));
				message.put("responseCode", EnumUtil.ResponseCode.请先登录.value);
			}
		}
		return result;
	}
	
	/**
	 * 从message中解析json数据
	 * @param message
	 * @return
	 */
	protected JSONObject getJsonFromMessage(Map<String, Object> message){
		JSONObject json = null;
		if(!CollectionUtils.isEmpty(message) && message.containsKey("json")){
			json = (JSONObject) message.get("json");
		}
		return json;
	}
	
	/**
	 * 从message中解析user数据
	 * @param message
	 * @return
	 */
	protected UserBean getUserFromMessage(Map<String, Object> message){
		UserBean user = null;
		if(!CollectionUtils.isEmpty(message) && message.containsKey("user")){
			user = (UserBean) message.get("user");
		}
		return user;
	}
	
	/**
	 * 将请求参数转化成json对象
	 * @param map
	 * @return
	 */
	private JSONObject convertParameterMapToJsonObject(Map<String, String[]> map){
		JSONObject json = new JSONObject();
		for(Entry<String, String[]> entry: map.entrySet()){
			//说明是数组
			if(entry.getValue().length > 1)
				json.put(entry.getKey(), entry.getValue());
			else 
				json.put(entry.getKey(), entry.getValue()[0]);
		}
		return json;
	}
}
