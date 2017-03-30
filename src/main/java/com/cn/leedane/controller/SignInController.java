package com.cn.leedane.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cn.leedane.exception.ErrorException;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.SignInBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.SignInService;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.DateUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.JsonUtil;
import com.cn.leedane.utils.ResponseMap;

@RestController
@RequestMapping(value = ControllerBaseNameUtil.si)
public class SignInController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());
	
	@Autowired
	private SignInService<SignInBean> signInService;
	
	// 操作日志
	@Autowired
	protected OperateLogService<OperateLogBean> operateLogService;
	
	/**
	 * 执行签到的主方法
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/signIn", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> signIn(HttpServletRequest request){
		//保存签到记录
		//更新积分
		//更新操作日志
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		int id = JsonUtil.getIntValue(getJsonFromMessage(message), "id", getUserFromMessage(message).getId());
		message.put("isSuccess", signInService.saveSignIn(getJsonFromMessage(message), userService.findById(id), request));
		return message.getMap();
	}
	
	/**
	 * 判断当天是否已经登录
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/currentDateIsSignIn", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> currentDateIsSignIn(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		message.put("isSuccess", true);
		JSONObject json = getJsonFromMessage(message);
		UserBean user = getUserFromMessage(message);
		if(json.has("id")) {
			int id = JsonUtil.getIntValue(getJsonFromMessage(message), "id", getUserFromMessage(message).getId());
			String dateTime = DateUtil.DateToString(new Date(), "yyyy-MM-dd");		
			message.put("isSuccess", signInService.isSign(id, dateTime));
			
			// 保存操作日志信息
			String subject = user.getAccount()+"判断当天是否签到";
			this.operateLogService.saveOperateLog(user, request, new Date(), subject, "currentDateIsSignIn", 1, 0);
			return message.getMap();
		}
		
		throw new ErrorException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.缺少请求参数.value));
	}
	
	/**
	 * 获取签到历史记录
	 * @return
	 */
	@RequestMapping(value = "/signIns", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> paging(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		List<Map<String, Object>> result= signInService.getSignInByLimit(getJsonFromMessage(message), getUserFromMessage(message), request);
		System.out.println("获得签到的数量：" +result.size());
		message.put("isSuccess", true);
		message.put("message", result);
		return message.getMap();
	}
}
