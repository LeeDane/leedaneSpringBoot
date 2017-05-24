package com.cn.leedane.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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

	private Logger logger = Logger.getLogger(getClass());
	
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
	public Map<String, Object> paging(@RequestParam(value="pageSize", required = false) int pageSize,
			@RequestParam(value="last_id", required = false) int lastId,
			@RequestParam(value="first_id", required = false) int firstId,
			@RequestParam(value="method", required = true) String method,
			HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		checkRoleOrPermission(request);
		
		List<Map<String, Object>> result= materialService.getMaterialByLimit(getJsonFromMessage(message), getUserFromMessage(message), request);
		logger.info("获得素材的数量：" +result.size());
		message.put("isSuccess", true);
		message.put("message", result);
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
