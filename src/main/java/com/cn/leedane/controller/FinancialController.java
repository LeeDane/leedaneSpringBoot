package com.cn.leedane.controller;

import com.cn.leedane.model.FinancialBean;
import com.cn.leedane.service.FinancialService;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.ResponseMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 记账控制器
 * @author LeeDane
 * 2016年7月22日 上午8:36:50
 * Version 1.0
 */
@RestController
@RequestMapping(value = ControllerBaseNameUtil.fn)
public class FinancialController extends BaseController{
	
	@Autowired
	private FinancialService<FinancialBean> financialService;
	
	/**
     * 客户端数据同步
     * @return 返回成功插入数据库的ID
     */
	@RequestMapping(value = "/financial", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    public Map<String, Object> save(Model model, HttpServletRequest request) {
    	ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(financialService.save(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
    }
	
	/**
     * 客户端数据更新
     * @return
     */
	@RequestMapping(value = "/financial", method = RequestMethod.PUT, produces = {"application/json;charset=UTF-8"})
    public Map<String, Object> update(Model model, HttpServletRequest request) {
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(financialService.update(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
    }
	
	/**
     * 客户端数据删除
     * @return
     */
	@RequestMapping(value = "/financial", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"})
    public Map<String, Object> delete(Model model, HttpServletRequest request) {
    	ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(financialService.delete(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
    }
	
	/**
     * 客户端数据同步
     * @return 返回成功同步的数量和有冲突的数据ID数组
     */
	@RequestMapping(value = "/synchronous", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
    public Map<String, Object> synchronous(Model model, HttpServletRequest request) {
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(financialService.synchronous(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
    }
    
    /**
     * 客户端强制更新数据(
     * 		在synchronous()后返回的冲突数据进行强制以客户端或者服务器端的为主，
     * 		要是以客户端的为主，将删掉服务器端的数据；
     * 		要是以服务器端的为主，将返回服务器端的数据，这时可以端需要做的就是替换掉客户端本地
     * 		数据为服务器端返回的数据。
     * )
     * 
     * @return
     */
	@RequestMapping(value = "/force", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
    public Map<String, Object> force(Model model, HttpServletRequest request) {
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(financialService.force(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
    }
	
	/**
	 * 获取该用户指定年份的数据
     * @return
     */
	@RequestMapping(value = "/byYear", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
    public Map<String, Object> getByYear(Model model, HttpServletRequest request) {
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(financialService.getByYear(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
    }
	
	/**
	 * 获取该用户全部的数据
     * @return
     */
	@RequestMapping(value = "/all", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
    public Map<String, Object> getAll(Model model, HttpServletRequest request) {
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(financialService.getAll(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
    }
	
	/**
	 * 搜索查询
     * @return
     */
	@RequestMapping(value = "/query", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
    public Map<String, Object> query(Model model, HttpServletRequest request) {
    	ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(financialService.query(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
    }
	
	/**
	 * 分页查询
     * @return
     */
	@RequestMapping(value = "/financials", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
    public Map<String, Object> paging(Model model, HttpServletRequest request) {
    	ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(financialService.paging(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
    }
}
