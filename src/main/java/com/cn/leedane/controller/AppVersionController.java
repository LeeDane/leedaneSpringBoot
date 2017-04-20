package com.cn.leedane.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cn.leedane.model.FilePathBean;
import com.cn.leedane.service.AppVersionService;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.ResponseMap;

@RestController
@RequestMapping(value = ControllerBaseNameUtil.av)
public class AppVersionController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());
	//上传filePath表的service
	@Autowired
	private AppVersionService<FilePathBean> appVersionService;
	
	/**
	 * 获取APP的最新版本信息
	 * @return
	 */
	@RequestMapping(value = "/newest", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> getNewest(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		
		message.putAll(appVersionService.getNewest(getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();	
	}
	
	/**
	 * 获取APP的版本列表信息
	 * @return
	 */
	@RequestMapping(value = "/versions", method = RequestMethod.GET)
	public Map<String, Object> paging(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		
		message.putAll(appVersionService.paging(getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
}
