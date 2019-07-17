package com.cn.leedane.controller.stock;

import com.cn.leedane.controller.BaseController;
import com.cn.leedane.model.stock.StockBuyBean;
import com.cn.leedane.service.stock.StockBuyService;
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
 * 股票购买接口controller
 * @author LeeDane
 * 2018年6月22日 下午5:47:52
 * version 1.0
 */
@RestController
@RequestMapping(value = ControllerBaseNameUtil.stock)
public class StockBuyController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());

	@Autowired
	private StockBuyService<StockBuyBean> stockBuyService;
		
	
	/**
	 * 新增股票购买记录
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/{stockId}/buy/add", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> add(@PathVariable("stockId") int stockId, Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(stockBuyService.add(stockId, getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 修改股票购买记录
	 * @return
	 */
	@RequestMapping(value = "/{stockId}/buy/{stockBuyId}", method = RequestMethod.PUT, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> update(@PathVariable("stockId") int stockId, @PathVariable("stockBuyId") int stockBuyId, Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(stockBuyService.update(stockId, stockBuyId, getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 删除股票购买记录
	 * @return
	 */
	@RequestMapping(value = "/{stockId}/buy/{stockBuyId}", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> delete(@PathVariable("stockId") int stockId, @PathVariable("stockBuyId") int stockBuyId, Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(stockBuyService.delete(stockId, stockBuyId, getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
		
	
}
