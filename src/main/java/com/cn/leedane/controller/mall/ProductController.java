package com.cn.leedane.controller.mall;

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
import com.cn.leedane.model.mall.S_ProductBean;
import com.cn.leedane.service.mall.S_ProductService;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.ResponseMap;

/**
 * 商品接口controller
 * @author LeeDane
 * 2017年11月7日 下午4:43:56
 * version 1.0
 */
@RestController
@RequestMapping(value = ControllerBaseNameUtil.mall)
public class ProductController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());

	@Autowired
	private S_ProductService<S_ProductBean> productService;
		
	/**
	 * 检查是否能创建圈子
	 * @return
	 */
	/*@RequestMapping(value = "/check", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> check(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(circleService.check(getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}*/
	
	/**
	 * 发布商品
	 * @return
	 */
	@RequestMapping(value = "/product", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> add(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(productService.save(getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
	
	
	/**
	 * 商品统计
	 * @return
	 */
	@RequestMapping(value = "/product/{productId}/statistics", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> statistics(@PathVariable("productId") int productId, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		
		message.putAll(productService.statistics(productId, getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
	
	/**
	 * 商品推荐
	 * @return
	 */
	@RequestMapping(value = "/product/{productId}/recommend", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> recommend(@PathVariable("productId") int productId, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		
		message.putAll(productService.recommend(productId, getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
	
	
	/**
	 * 修改圈子
	 * @return
	 */
	/*@RequestMapping(value = "/circle", method = RequestMethod.PUT, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> update(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(circleService.update(getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}*/
	
	/**
	 * 获取圈子分页列表
	 * @return
	 */
	/*@RequestMapping(value = "/circles", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> paging(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(circleService.paging(getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}*/
	
	/**
	 * 删除圈子
	 * @return
	 */
	/*@RequestMapping(value = "/circle/{cid}", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> delete(Model model, HttpServletRequest request, @PathVariable("cid") int cid){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(circleService.delete(cid, getUserFromMessage(message), request));
		return message.getMap();
	}*/
	
	/**
	 * 申请加入圈子之前判断是否可以加入
	 * @return
	 */
	/*@RequestMapping(value = "/join/check", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> joinCheck(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(circleService.joinCheck(getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}*/
	
	/**
	 * 申请加入圈子
	 * @return
	 */
	/*@RequestMapping(value = "/join", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> join(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(circleService.join(getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}*/
	
	/**
	 * 离开圈子
	 * @return
	 */
	/*@RequestMapping(value = "/leave/{circleId}", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> leave(@PathVariable("circleId") int circleId, Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(circleService.leave(circleId, getUserFromMessage(message), request));
		return message.getMap();
	}
	*/
	/**
	 * 分页获取权限列表
	 * @return
	 */
	/*@RequestMapping(value = "/circle/{cid}/admins", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> roles(Model model, HttpServletRequest request, @PathVariable("cid") int cid){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(circleService.admins(cid, getUserFromMessage(message), request));
		return message.getMap();
	}*/
	
	/**
	 * 角色的分配
	 * @return
	 */
	/*@RequestMapping(value = "/circle/{cid}/admins/allot", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> allot(Model model, HttpServletRequest request, @PathVariable("cid") int cid){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		JSONObject json = getJsonFromMessage(message);
		String admins = JsonUtil.getStringValue(json, "admins");
		
		message.putAll(circleService.allot(cid, admins, getUserFromMessage(message), request));
		return message.getMap();
	}*/
	
	/**
	 * 圈子首页的一些初始化参数获取
	 * @return
	 */
	/*@RequestMapping(value = "/circle/{cid}/init", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> initialize(Model model, HttpServletRequest request, @PathVariable("cid") int cid){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(circleService.initialize(cid, getUserFromMessage(message), request));
		return message.getMap();
	}*/
}
