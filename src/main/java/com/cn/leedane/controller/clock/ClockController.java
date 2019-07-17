package com.cn.leedane.controller.clock;

import java.util.HashMap;
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
import com.cn.leedane.model.clock.ClockBean;
import com.cn.leedane.service.clock.ClockService;
import com.cn.leedane.utils.CommonUtil;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.ResponseMap;

/**
 * 提醒接口controller
 * @author LeeDane
 * 2018年8月29日 上午10:29:37
 * version 1.0
 */
@RestController
@RequestMapping(value = ControllerBaseNameUtil.clock)
public class ClockController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());

	@Autowired
	private ClockService<ClockBean> clockService;
		
	/**
	 * 添加任务
	 * @return
	 */
	@RequestMapping(value = "/clock", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> add(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(clockService.add(getJsonFromMessage(message), getMustLoginUserFromShiro(), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 修改任务
	 * @return
	 */
	@RequestMapping(value = "/{clockId}", method = RequestMethod.PUT, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> update(@PathVariable("clockId") int clockId, Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(clockService.update(clockId, getJsonFromMessage(message), getMustLoginUserFromShiro(), getHttpRequestInfo(request)));
		return message.getMap();
	}
	/**
	 * 获取任务的信息
	 * @return
	 */
	@RequestMapping(value = "/{clockId}/info", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> getClock(@PathVariable("clockId") int clockId, Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(clockService.getClock(clockId, getJsonFromMessage(message), getMustLoginUserFromShiro(), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 获取任务的缩略信息
	 * @return
	 */
	@RequestMapping(value = "/{clockId}/thumbnail", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> getClockThumbnail(@PathVariable("clockId") int clockId, Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(clockService.getClockThumbnail(clockId, getJsonFromMessage(message), getMustLoginUserFromShiro(), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	
	/**
	 * 搜索任务(只支持搜索共享的任务或者是自己的任务，返回最多10条记录)
	 * @return
	 */
	@RequestMapping(value = "/search", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> search(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(clockService.search(getJsonFromMessage(message), getMustLoginUserFromShiro(), getHttpRequestInfo(request)));
		return message.getMap();
	}
//	
//	/**
//	 * 修改圈子
//	 * @return
//	 */
//	@RequestMapping(value = "/{circleId}", method = RequestMethod.PUT, produces = {"application/json;charset=UTF-8"})
//	public Map<String, Object> update(@PathVariable("circleId") int circleId, Model model, HttpServletRequest request){
//		ResponseMap message = new ResponseMap();
//		if(!checkParams(message, request))
//			return message.getMap();
//		
//		checkRoleOrPermission(model, request);
//		message.putAll(circleService.update(circleId, getJsonFromMessage(message), getMustLoginUserFromShiro(), getHttpRequestInfo(request)));
//		return message.getMap();
//	}
	
	/**
	 * 删除任务
	 * @return
	 */
	@RequestMapping(value = "/{clockId}", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> delete(Model model, HttpServletRequest request, @PathVariable("clockId") int clockId){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(clockService.delete(clockId, getMustLoginUserFromShiro(), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 获取指定日期的打卡任务的列表
	 * @return
	 */
	@RequestMapping(value = "/date/{date}/clocks", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> dateClocks(@PathVariable("date")String date, Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(clockService.dateClocks(date, getMustLoginUserFromShiro(), getHttpRequestInfo(request)));
		//int i = 10 / 0;
		return message.getMap();
	}
	
	/**
	 * 获取自己在进行中的列表
	 * @return
	 */
	@RequestMapping(value = "/ongoings", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> ongoings(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(clockService.getOngoingClocks(getMustLoginUserFromShiro(), getJsonFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 获取自己结束的列表
	 * @return
	 */
	@RequestMapping(value = "/endeds", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> endeds(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(clockService.getEndeds(getMustLoginUserFromShiro(), getJsonFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 获取系统默认任务的列表
	 * @return
	 */
	@RequestMapping(value = "/system/clocks", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> dateClocks(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(clockService.systemClocks(getMustLoginUserFromShiro(), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 获取任务基本统计信息
	 * @param clockId
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/{clockId}/statistics", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> dynamics(@PathVariable("clockId") int clockId, Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(clockService.statistics(clockId, getJsonFromMessage(message), getMustLoginUserFromShiro(), getHttpRequestInfo(request)));
		return message.getMap();
	}

	/**
	 * 获取任务资源列表
	 * @param clockId
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/{clockId}/type/{resourceType}/resources", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> resources(@PathVariable("clockId") int clockId, @PathVariable("resourceType") int resourceType, Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();

		checkRoleOrPermission(model, request);
		message.putAll(clockService.resources(clockId, resourceType, getJsonFromMessage(message), getMustLoginUserFromShiro(), getHttpRequestInfo(request)));
		return message.getMap();
	}

	/**
	 * 捐赠页面
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/donate", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> donate(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		checkParams(message, request);

		Map<String, Object> msg = new HashMap<String, Object>();
		message.put("isSuccess", true);
		message.put("message", msg);
		msg.put("tip", "<font color='#ff0000'>官宣</font>：本app是一款致力于解决个人日常提醒事务的助手，再次承诺，本app使用完全免费。管理员、开发者等绝对不会也不能以个人名义向使用者索取非法的资金。由于这个是纯免费的应用， 并且持续开发精力和时间有限，您们对我的鼓励将更大激励我进行版本更新和功能改进。谢谢！为了感谢您的捐赠，请捐赠者在捐赠完成后保留本次捐赠截图，并在问题反馈页面选择捐赠类别提交给管理员，管理员将把您的名字放到捐赠榜中。");
		msg.put("wxqrcode", "http://pic.onlyloveu.top/leedane_20190128172847%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20190128172814.jpg?imageslim");
		msg.put("wxtip", "请使用微信扫一扫向我捐赠");
		msg.put("zfbqrcode", "http://pic.onlyloveu.top/leedane_20190128172906%E5%BE%AE%E4%BF%A1%E5%9B%BE%E7%89%87_20190128172855.jpg?imageslim");
		msg.put("zfbtip", "请使用支付宝扫一扫向我捐赠");
		return message.getMap();
	}
}
