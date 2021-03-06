package com.cn.leedane.controller;

import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.ResponseMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 操作日志controller
 */
@RestController
@RequestMapping(value = ControllerBaseNameUtil.ol)
public class OperateLogController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());
	
	// 操作日志
	@Autowired
	protected OperateLogService<OperateLogBean> operateLogService;
	
	/**
	 * 分页获取用户登录操作日志列表
	 * @return
	 */
	@RequestMapping(value = "/logins", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> loginPaging(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(operateLogService.getUserLoginLimit(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
}
