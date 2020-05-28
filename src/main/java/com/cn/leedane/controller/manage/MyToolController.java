package com.cn.leedane.controller.manage;

import com.cn.leedane.controller.BaseController;
import com.cn.leedane.service.manage.MyToolService;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.ResponseMap;
import com.cn.leedane.utils.ResponseModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 我的工具入口接口controller
 * @author LeeDane
 * 2019年11月12日 下午6:30:40
 * version 1.0
 */
@RestController
@RequestMapping(value = ControllerBaseNameUtil.myManage)
public class MyToolController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());

	@Autowired
	private MyToolService myToolService;

	/**
	 * 抖音视频去水印
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/tool/douyin/remove/watermark", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public ResponseModel removeWatermark(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		return myToolService.removeWatermark(getJsonFromMessage(message), getMustLoginUserFromShiro(), getHttpRequestInfo(request));
	}

	/**
	 * 添加事件提醒
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/tool/event/remind", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public ResponseModel addRemind(HttpServletRequest request) throws SchedulerException {
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		return myToolService.addRemind(getJsonFromMessage(message), getMustLoginUserFromShiro(), getHttpRequestInfo(request));
	}

	/**
	 * 删除事件提醒
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/tool/event/remind/{remindId}", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"})
	public ResponseModel deleteRemind(@PathVariable("remindId") long remindId, HttpServletRequest request) throws SchedulerException {
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		return myToolService.deleteRemind(remindId, getJsonFromMessage(message), getMustLoginUserFromShiro(), getHttpRequestInfo(request));
	}

	/**
	 * 事件提醒列表
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/tool/event/reminds", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public ResponseModel reminds(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		return myToolService.reminds(getJsonFromMessage(message), getMustLoginUserFromShiro(), getHttpRequestInfo(request));
	}
}
