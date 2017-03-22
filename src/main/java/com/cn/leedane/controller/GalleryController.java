package com.cn.leedane.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cn.leedane.model.GalleryBean;
import com.cn.leedane.service.GalleryService;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.ResponseMap;

@RestController
@RequestMapping("/gl")
public class GalleryController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());
	
	@Autowired
	private GalleryService<GalleryBean> galleryService;
	
	
	/**
	 * 添加网络链接到图库
	 * @return
	 */
	
	@RequestMapping("/addLink")
	public Map<String, Object> addLink(HttpServletRequest request, HttpServletResponse response){
		ResponseMap message = new ResponseMap();
		try {
			if(!checkParams(message, request)){
				return message.getMap();
			}
			message.putAll(galleryService.addLink(getJsonFromMessage(message), getUserFromMessage(message), request));
			return message.getMap();
		} catch (Exception e) {
			e.printStackTrace();
		}
		message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		return message.getMap();
	}
	
	/**
	 * 获取图库的照片列表
	 * @return
	 */
	@RequestMapping(value = "/photos", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"}) 
	public Map<String, Object> paging(@RequestParam(value="pageSize", required = false) int pageSize,
			@RequestParam(value="last_id", required = false) int lastId,
			@RequestParam(value="first_id", required = false) int firstId,
			@RequestParam(value="method", required = true) String method,
			HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		try {
			if(!checkParams(message, request))
				return message.getMap();
			
			List<Map<String, Object>> result= galleryService.getGalleryByLimit(getJsonFromMessage(message), getUserFromMessage(message), request);
			System.out.println("获得图库的数量：" +result.size());
			message.put("isSuccess", true);
			message.put("message", result);
			return message.getMap();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return message.getMap();
	}
	

	/**
	 * 移出图库
	 * @return
	 */
	@RequestMapping(value = "/photo/{gid}", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"}) 
	public String delete(HttpServletRequest request, HttpServletResponse response){
		Map<String, Object> message = new HashMap<String, Object>();
		long start = System.currentTimeMillis();
		try {
			if(!checkParams(message, request)){
				printWriter(message, response, start);
				return null;
			}
			message.putAll(galleryService.delete(getJsonFromMessage(message), getUserFromMessage(message), request));
			printWriter(message, response, start);
			return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		printWriter(message, response, start);
		return null;
	}
}
