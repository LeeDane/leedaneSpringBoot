package com.cn.leedane.controller.stock;

import com.cn.leedane.controller.BaseController;
import com.cn.leedane.model.stock.StockBean;
import com.cn.leedane.service.stock.StockService;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.ResponseMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 股票接口controller
 * @author LeeDane
 * 2017年6月11日 下午4:21:49
 * version 1.0
 */
@RestController
@RequestMapping(value = ControllerBaseNameUtil.stock)
public class StockController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());

	@Autowired
	private StockService<StockBean> stockService;
		
	/**
	 * 股票首页初始化
	 * @return
	 */
	@RequestMapping(value = "/init", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> check(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(stockService.init(getJsonFromMessage(message), getMustLoginUserFromShiro(), getHttpRequestInfo(request)));
		
		
		return message.getMap();
	}
	
	/**
	 * 新增股票
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> add(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(stockService.add(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 修改股票
	 * @return
	 */
	@RequestMapping(value = "/{stockId}", method = RequestMethod.PUT, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> update(@PathVariable("stockId") long stockId, Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(stockService.update(stockId, getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 删除股票
	 * @return
	 */
	@RequestMapping(value = "/{stockId}", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> delete(@PathVariable("stockId") long stockId, Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(stockService.delete(stockId, getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
		
	
}
