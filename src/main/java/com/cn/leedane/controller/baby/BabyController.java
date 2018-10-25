package com.cn.leedane.controller.baby;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cn.leedane.controller.BaseController;
import com.cn.leedane.model.baby.BabyBean;
import com.cn.leedane.model.baby.BabyLifeBean;
import com.cn.leedane.service.baby.BabyLifeService;
import com.cn.leedane.service.baby.BabyService;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.ResponseMap;

/**
 * 宝宝接口controller
 * @author LeeDane
 * 2018年6月4日 下午5:20:30
 * version 1.0
 */
@RestController
@RequestMapping(value = ControllerBaseNameUtil.baby)
public class BabyController extends BaseController{

	@Autowired
	private BabyService<BabyBean> babyService;
	
	@Autowired
	private BabyLifeService<BabyLifeBean> babyLifeService;
	
	
	/**
	 * 新增我的宝宝
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> add(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(babyService.add(getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
	
	/**
	 * 修改我的宝宝
	 * @return
	 */
	@RequestMapping(value = "/{babyId}", method = RequestMethod.PUT, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> update(@PathVariable("babyId") int babyId, Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(babyService.update(babyId, getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
	

	/**
	 * 删除我的宝宝
	 * @return
	 */
	@RequestMapping(value = "/{babyId}", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> delete(@PathVariable("babyId") int babyId, Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(babyService.delete(babyId, getUserFromMessage(message), request));
		return message.getMap();
	}
	
	/**
	 * 添加生活方式模板
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/{babyId}/life", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> addLife(
			@PathVariable(value="babyId") int babyId,
			Model model, 
			HttpServletRequest request){
		
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(babyLifeService.add(babyId, getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
	
	/**
	 * 修改生活方式模板
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/{babyId}/life/{lifeId}", method = RequestMethod.PUT, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> updateLife(
			@PathVariable(value="babyId") int babyId,
			@PathVariable(value="lifeId") int lifeId,
			Model model, 
			HttpServletRequest request){
		
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(babyLifeService.update(babyId, lifeId, getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
	
	/**
	 * 改变宝宝的状态为出生状态
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/{babyId}/changeBorn", method = RequestMethod.PUT, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> changeBorn(
			@PathVariable(value="babyId") int babyId,
			Model model, 
			HttpServletRequest request){
		
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(babyService.changeBorn(babyId, getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
	
	/**
	 * 删除生活方式模板
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/{babyId}/life/{lifeId}", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> addEat(
			@PathVariable(value="babyId") int babyId,
			@PathVariable(value="lifeId") int lifeId,
			Model model, 
			HttpServletRequest request){
		
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(babyLifeService.delete(babyId, lifeId, getUserFromMessage(message), request));
		return message.getMap();
	}
	
	
	/**
	 * 获取宝宝的生活方式列表
	 * @param babyId
	 * @param startDate
	 * @param endDate
	 * @param keyWord
	 * @param lifeType
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/{babyId}/lifes", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> getLifes(
			@PathVariable(value="babyId") int babyId,
			@RequestParam(value="start", required=false) String startDate,
			@RequestParam(value="end", required=false) String endDate,
			@RequestParam(value="keyword", required=false) String keyWord,
			@RequestParam(value="type", required=false, defaultValue = "0") Integer lifeType,
			Model model, 
			HttpServletRequest request){
		
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);
		message.putAll(babyLifeService.lifes(babyId, startDate, endDate, keyWord, lifeType, getUserFromMessage(message), request));
		return message.getMap();
	}
}
