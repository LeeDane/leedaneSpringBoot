package com.cn.leedane.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cn.leedane.model.MaterialBean;
import com.cn.leedane.service.MaterialService;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.ResponseMap;
/**
 * 素材相关的控制器
 * @author LeeDane
 * 2017年5月22日 上午10:18:08
 * version 1.0
 */
@RestController
@RequestMapping(value = ControllerBaseNameUtil.mt)
public class MaterialController extends BaseController{
	
	@Autowired
	private MaterialService<MaterialBean> materialService;
	
	
	/**
	 * 添加网络链接到素材
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "/material", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> addMaterials(HttpServletRequest request) throws Exception{
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request)){
			return message.getMap();
		}
		checkRoleOrPermission(request);
		
		message.putAll(materialService.save(getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
		
	}
	
	/**
	 * 获取素材的照片列表
	 * @return
	 */
	@RequestMapping(value = "/materials", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"}) 
	public Map<String, Object> paging(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		checkRoleOrPermission(request);
		
		message.putAll(materialService.getMaterialByLimit(getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
	

	/**
	 * 移出素材
	 * @return
	 */
	@RequestMapping(value = "/material/{mid}", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"}) 
	public Map<String, Object> delete(HttpServletRequest request, @PathVariable("mid") int materialId){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		checkRoleOrPermission(request);
		
		message.putAll(materialService.delete(materialId, getUserFromMessage(message), request));
		return message.getMap();
	}
}