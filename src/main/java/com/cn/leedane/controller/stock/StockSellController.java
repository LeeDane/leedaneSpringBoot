package com.cn.leedane.controller.stock;

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
import com.cn.leedane.model.stock.StockSellBean;
import com.cn.leedane.service.stock.StockSellService;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.ResponseMap;

/**
 * 股票卖出接口controller
 * @author LeeDane
 * 2018年6月22日 下午5:50:35
 * version 1.0
 */
@RestController
@RequestMapping(value = ControllerBaseNameUtil.stock)
public class StockSellController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());

	@Autowired
	private StockSellService<StockSellBean> stockSellService;
		
	
	/**
	 * 新增股票卖出记录
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/{stockId}/buy/{stockBuyId}/sell/add", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> add(@PathVariable("stockId") int stockId, @PathVariable("stockBuyId") int stockBuyId, Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(stockSellService.add(stockId, stockBuyId, getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
	
	/**
	 * 修改股票卖出记录
	 * @return
	 */
	@RequestMapping(value = "/{stockId}/buy/{stockBuyId}/sell/{stockSellId}", method = RequestMethod.PUT, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> update(@PathVariable("stockId") int stockId, @PathVariable("stockBuyId") int stockBuyId, @PathVariable("stockSellId") int stockSellId, Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(stockSellService.update(stockId, stockBuyId, stockSellId, getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
	
	/**
	 * 删除股票卖出记录
	 * @return
	 */
	@RequestMapping(value = "/{stockId}/buy/{stockBuyId}/sell/{stockSellId}", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> delete(@PathVariable("stockId") int stockId, @PathVariable("stockBuyId") int stockBuyId, @PathVariable("stockSellId") int stockSellId, Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(stockSellService.delete(stockId, stockBuyId, stockSellId, getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
}
