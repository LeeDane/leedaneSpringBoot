package com.cn.leedane.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cn.leedane.model.ChatSquareBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.service.ChatSquareService;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.DateUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.ResponseMap;

@RestController
@RequestMapping(value = ControllerBaseNameUtil.cs)
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
	@RequestMapping(value = "/active", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> getActiveUser(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		
		message.putAll(chatSquareService.getActiveUser(DateUtil.getTodayStart(), 8));
		message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		return message.getMap();
	}
}
