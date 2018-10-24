package com.cn.leedane.controller.clock;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cn.leedane.controller.BaseController;
import com.cn.leedane.model.clock.ClockInBean;
import com.cn.leedane.service.clock.ClockInService;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.ResponseMap;

/**
 * 打卡接口controller
 * @author LeeDane
 * 2018年9月11日 下午9:52:13
 * version 1.0
 */
@RestController
@RequestMapping(value = ControllerBaseNameUtil.clockIn)
public class ClockInController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());

	@Autowired
	private ClockInService<ClockInBean> clockInService;
	
	/**
	 * 获取指定日期的打卡任务的列表
	 * @return
	 */
	@RequestMapping(value = "", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> dateClocks(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(clockInService.add(getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
	
}