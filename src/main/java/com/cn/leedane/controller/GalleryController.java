package com.cn.leedane.controller;

import com.cn.leedane.model.GalleryBean;
import com.cn.leedane.service.GalleryService;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.ResponseMap;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = ControllerBaseNameUtil.gl)
public class GalleryController extends BaseController{

	private Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private GalleryService<GalleryBean> galleryService;
	
	
	/**
	 * 添加网络链接到图库
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "/photo", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> manageLink(Model model, HttpServletRequest request) throws Exception{
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request)){
			return message.getMap();
		}
		
		checkRoleOrPermission(model, request);;
		message.putAll(galleryService.manageLink(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	
	/**
	 * 获取图库的照片列表
	 * @return
	 */
	@RequestMapping(value = "/all", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> all(@RequestParam(value="pageSize", required = false) int pageSize,
			@RequestParam(value="last_id", required = false) int lastId,
			@RequestParam(value="first_id", required = false) int firstId,
			@RequestParam(value="method", required = true) String method,
			Model model, 
			HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();

		checkRoleOrPermission(model, request);;
		
		List<Map<String, Object>> result= galleryService.all(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request));
		logger.info("获得图库的数量：" +result.size());
		message.put("isSuccess", true);
		message.put("message", result);
		return message.getMap();
	}

	/**
	 * 获取图库的照片列表
	 * @return
	 */
	@RequestMapping(value = "/photos", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> paging(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();

		checkRoleOrPermission(model, request);;
		message.putAll(galleryService.paging(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
	

	/**
	 * 移出图库
	 * @return
	 */
	@RequestMapping(value = "/photo/{gid}", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"}) 
	public Map<String, Object> delete(@PathVariable("gid") long gid, Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(galleryService.delete(gid, getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
}
