package com.cn.leedane.controller;

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

import com.cn.leedane.model.VisitorBean;
import com.cn.leedane.service.VisitorService;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.ResponseMap;

/**
 * 访客控制器
 * @author LeeDane
 * 2017年5月11日 下午4:40:25
 * version 1.0
 */
@RestController
@RequestMapping(value = ControllerBaseNameUtil.vt)
public class VisitorController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());

	//评论service
	@Autowired
	private VisitorService<VisitorBean> visitorService;
	
	/**
	 * 获取对象的访客列表
	 * @return
	 */
	@RequestMapping(value = "user/{tableId}/visitors", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> paging(@PathVariable("tableId") int tableId, Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(visitorService.getVisitorsByLimit(tableId, getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
}
