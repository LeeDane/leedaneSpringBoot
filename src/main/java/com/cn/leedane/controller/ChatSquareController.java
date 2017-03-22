package com.cn.leedane.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cn.leedane.model.ChatSquareBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.service.ChatSquareService;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.utils.DateUtil;
import com.cn.leedane.utils.EnumUtil;

@Controller
@RequestMapping("/leedane/chat/square")
public class ChatSquareController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());
	
	@Autowired
	private ChatSquareService<ChatSquareBean> chatSquareService;
	
	// 操作日志
	@Autowired
	protected OperateLogService<OperateLogBean> operateLogService;
	
	/**
	 * 获取系统当天活跃的用户列表
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getActiveUser")
	public String getActiveUser(HttpServletRequest request, HttpServletResponse response){
		Map<String, Object> message = new HashMap<String, Object>();
		long start = System.currentTimeMillis();
		try {
			checkParams(message, request);
			
			message.putAll(chatSquareService.getActiveUser(DateUtil.getTodayStart(), 8));
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
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
}
