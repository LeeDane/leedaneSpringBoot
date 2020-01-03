package com.cn.leedane.controller;

import com.cn.leedane.model.ReportBean;
import com.cn.leedane.service.ReportService;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.ResponseMap;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = ControllerBaseNameUtil.rp)
public class ReportController extends BaseController{

	private Logger logger = Logger.getLogger(getClass());

	//举报service
	@Autowired
	private ReportService<ReportBean> reportService;

	/**
	 * 添加举报
	 * @return
	 */
	@RequestMapping(value = "/report", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> add(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(reportService.addReport(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 取消举报
	 * @return
	 */
	@RequestMapping(value = "/report", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> cancel(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(reportService.cancel(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 获取举报列表
	 * @return
	 */
	@RequestMapping(value = "/reports", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> paging(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		List<Map<String, Object>> result= reportService.getLimit(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request));
		logger.info("获得举报的数量：" +result.size());
		message.put("success", true);
		message.put("message", result);
		return message.getMap();
	}
}
