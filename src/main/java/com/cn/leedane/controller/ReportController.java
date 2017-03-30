package com.cn.leedane.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cn.leedane.model.ReportBean;
import com.cn.leedane.service.ReportService;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.ResponseMap;

@Controller
@RequestMapping(value = ControllerBaseNameUtil.rp)
public class ReportController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());

	//举报service
	@Autowired
	private ReportService<ReportBean> reportService;

	/**
	 * 添加举报
	 * @return
	 */
	@RequestMapping(value = "/report", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> add(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		message.putAll(reportService.addReport(getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
	
	/**
	 * 取消举报
	 * @return
	 */
	@RequestMapping(value = "/report", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> cancel(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		message.putAll(reportService.cancel(getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
	
	/**
	 * 获取举报列表
	 * @return
	 */
	@RequestMapping(value = "/reports", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> paging(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		List<Map<String, Object>> result= reportService.getLimit(getJsonFromMessage(message), getUserFromMessage(message), request);
		System.out.println("获得举报的数量：" +result.size());
		message.put("isSuccess", true);
		message.put("message", result);
		return message.getMap();
	}
}
