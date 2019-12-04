package com.cn.leedane.controller.circle;

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
import com.cn.leedane.model.circle.CircleMemberBean;
import com.cn.leedane.model.circle.CircleSettingBean;
import com.cn.leedane.service.circle.CircleMemberService;
import com.cn.leedane.service.circle.CircleSettingService;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.ResponseMap;

/**
 * 圈子接口controller
 * @author LeeDane
 * 2017年6月11日 下午4:21:49
 * version 1.0
 */
@RestController
@RequestMapping(value = ControllerBaseNameUtil.cc)
public class CircleMemberController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());

	@Autowired
	private CircleMemberService<CircleMemberBean> circleMemberService;
	
	@Autowired
	private CircleSettingService<CircleSettingBean> circleSettingService;
		
	/**
	 * 获取圈子成员分页列表
	 * @return
	 */
	@RequestMapping(value = "/{circleId}/members", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> paging(@PathVariable("circleId") long circleId, Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(circleMemberService.paging(circleId, getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	
	/**
	 * 推荐/取消推荐某个成员（登录必须是圈主或者圈子管理员）
	 * @return
	 */
	@RequestMapping(value = "/{circleId}/member/{memberId}/recommend", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> recommend(@PathVariable("circleId") long circleId, @PathVariable("memberId") long memberId, Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(circleMemberService.recommend(circleId, memberId, getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 删除某个成员
	 * @return
	 */
	@RequestMapping(value = "/{circleId}/member/{memberId}", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> delete(@PathVariable("circleId") long circleId, @PathVariable("memberId") long memberId, Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(circleMemberService.delete(circleId, memberId, getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 圈子的设置(圈主或者圈子管理员才能设置)
	 * @return
	 */
	@RequestMapping(value = "/{circleId}/setting/{settingId}", method = RequestMethod.PUT, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> setting(@PathVariable("circleId") long circleId, @PathVariable("settingId") long settingId, Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(circleSettingService.update(circleId, settingId, getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
}
