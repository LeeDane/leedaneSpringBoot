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
import com.cn.leedane.model.clock.ClockMemberBean;
import com.cn.leedane.service.clock.ClockMemberService;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.ResponseMap;

/**
 * 任务成员接口controller
 * @author LeeDane
 * 2018年9月11日 下午9:52:13
 * version 1.0
 */
@RestController
@RequestMapping(value = ControllerBaseNameUtil.clockMember)
public class ClockMemberController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());

	@Autowired
	private ClockMemberService<ClockMemberBean> clockMemberService;
	
	/**
	 * 任务成员列表
	 * @return
	 */
	@RequestMapping(value = "/{clockId}/members", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> members(@PathVariable("clockId") int clockId, Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(clockMemberService.members(clockId, getMustLoginUserFromShiro(), getHttpRequestInfo(request)));
		return message.getMap();
	}
}
