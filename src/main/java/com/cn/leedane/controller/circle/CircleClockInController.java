package com.cn.leedane.controller.circle;

import java.util.Date;
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
import com.cn.leedane.model.circle.CircleClockInBean;
import com.cn.leedane.service.circle.CircleClockInService;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.ResponseMap;

/**
 * 圈子打卡接口controller
 * @author LeeDane
 * 2017年6月14日 下午5:50:06
 * version 1.0
 */
@RestController
@RequestMapping(value = ControllerBaseNameUtil.cc)
public class CircleClockInController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());

	@Autowired
	private CircleClockInService<CircleClockInBean> circleClockInService;
		
	/**
	 * 判断当天是否打卡
	 * @return
	 */
	@RequestMapping(value = "/{circleId}/clockIn/check", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> check(@PathVariable("circleId")long circleId, Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(circleClockInService.isClockIn(getUserFromMessage(message), circleId, new Date()));
		return message.getMap();
	}
	
	/**
	 * 打卡
	 * @return
	 */
	@RequestMapping(value = "{circleId}/clockIn", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> add(@PathVariable("circleId")long circleId, Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(circleClockInService.saveClockIn(circleId, getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
}
