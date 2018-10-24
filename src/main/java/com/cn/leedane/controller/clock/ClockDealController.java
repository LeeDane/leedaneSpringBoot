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
import com.cn.leedane.model.clock.ClockDealBean;
import com.cn.leedane.model.clock.ClockMemberBean;
import com.cn.leedane.service.clock.ClockDealService;
import com.cn.leedane.service.clock.ClockMemberService;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.ResponseMap;

/**
 * 任务成员关系处理接口controller
 * @author LeeDane
 * 2018年10月23日 下午2:54:11
 * version 1.0
 */
@RestController
@RequestMapping(value = ControllerBaseNameUtil.clockDeal)
public class ClockDealController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());

	@Autowired
	private ClockDealService<ClockDealBean> clockDealService;
	
	/**
	 * 请求加入对方的任务(必须是共享的任务，并且人数没有超过共享人数，时间不能超过报名时间)
	 * @return
	 */
	@RequestMapping(value = "/request/{clockId}/add", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> requestAdd(@PathVariable("clockId") int clockId, Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(clockDealService.requestAdd(clockId, getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
	
	/**
	 * 同意某人加入任务(必须是共享的任务，并且人数没有超过共享人数，时间不能超过报名时间)
	 * @return
	 */
	@RequestMapping(value = "/request/{clockMemberId}/agree", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> requestAgree(@PathVariable("clockMemberId") int clockMemberId, Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(clockDealService.requestAgree(clockMemberId, getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
	
	/**
	 * 获取我的请求加入任务的列表
	 * @return
	 */
	@RequestMapping(value = "/addClocks", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> addClocks(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(clockDealService.addClocks(getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
	
	/**
	 * 获取邀请我加入任务的列表
	 * @return
	 */
	@RequestMapping(value = "/inviteClocks", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> inviteClocks(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(clockDealService.inviteClocks(getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
	
	/**
	 * 获取我邀请的任务的列表
	 * @return
	 */
	@RequestMapping(value = "/myInviteClocks", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> myInviteClocks(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(clockDealService.myInviteClocks(getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
	
	/**
	 * 获得等待我的同意的任务列表
	 * @return
	 */
	@RequestMapping(value = "/agreeClocks", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> agreeClocks(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(clockDealService.agreeClocks(getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
}
