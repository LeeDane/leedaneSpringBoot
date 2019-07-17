package com.cn.leedane.controller.clock;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cn.leedane.controller.BaseController;
import com.cn.leedane.model.clock.ClockDynamicBean;
import com.cn.leedane.service.clock.ClockDynamicService;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.ResponseMap;

/**
 * 任务动态处理接口controller
 * @author LeeDane
 * 2018年11月27日 上午9:22:31
 * version 1.0
 */
@RestController
@RequestMapping(value = ControllerBaseNameUtil.clockDynamic)
public class ClockDynamicController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());

	@Autowired
	private ClockDynamicService<ClockDynamicBean> clockDynamicService;
	
	/**
	 * 获取任务动态列表
	 * @param clockId
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/{clockId}/dynamics", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> dynamics(@PathVariable("clockId") int clockId, Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(clockDynamicService.dynamics(clockId, getJsonFromMessage(message), getMustLoginUserFromShiro(), getHttpRequestInfo(request)));
		return message.getMap();
	}
}
