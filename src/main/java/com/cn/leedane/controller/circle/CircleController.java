package com.cn.leedane.controller.circle;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cn.leedane.controller.BaseController;
import com.cn.leedane.model.circle.CircleBean;
import com.cn.leedane.service.circle.CircleService;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.JsonUtil;
import com.cn.leedane.utils.ResponseMap;

@RestController
@RequestMapping(value = ControllerBaseNameUtil.cc)
public class CircleController extends BaseController{

	private Logger logger = Logger.getLogger(getClass());
	
	
	@Autowired
	private CircleService<CircleBean> circleService;

	/**
	 * 添加圈子
	 * @return
	 */
	@RequestMapping(value = "/circle", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> add(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(request);
		message.putAll(circleService.create(getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
	
	/**
	 * 更新圈子
	 * @return
	 */
	@RequestMapping(value = "/circle", method = RequestMethod.PUT, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> update(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(request);
		message.putAll(circleService.update(getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
	
	/**
	 * 删除圈子
	 * @return
	 */
	@RequestMapping(value = "/circle/{cid}", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> delete(HttpServletRequest request, @PathVariable("cid") int cid){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(request);
		message.putAll(circleService.delete(cid, getUserFromMessage(message), request));
		return message.getMap();
	}
	
	/**
	 * 获取全部圈子列表
	 * @return
	 */
	@RequestMapping(value = "/circles", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> paging(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(request);
		//为了安全，必须是登录用户才能操作
		int toUserId = JsonUtil.getIntValue(getJsonFromMessage(message), "toUserId");
		if(toUserId != getUserFromMessage(message).getId()){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.没有操作权限.value));
			message.put("responseCode", EnumUtil.ResponseCode.没有操作权限.value);
			return message.getMap();
		}
		
		//List<Map<String, Object>> result= circleService.(getJsonFromMessage(message), getUserFromMessage(message), request);
		//logger.info("获得收藏的数量：" +result.size());
		message.put("isSuccess", true);
		//message.put("message", result);
		return message.getMap();
	}
}
