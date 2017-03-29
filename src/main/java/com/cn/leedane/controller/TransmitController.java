package com.cn.leedane.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cn.leedane.model.TransmitBean;
import com.cn.leedane.service.TransmitService;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.ResponseMap;

@RestController
@RequestMapping(value = ControllerBaseNameUtil.ts)
public class TransmitController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());
	//转发service
	@Autowired
	private TransmitService<TransmitBean> transmitService;

	/**
	 * 转发
	 * @return
	 */
	@RequestMapping(value = "/transmit", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> add(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		try {
			if(!checkParams(message, request))
				return message.getMap();
			
			message.putAll(transmitService.add(getJsonFromMessage(message), getUserFromMessage(message), request));
			return message.getMap();
		} catch (Exception e) {
			e.printStackTrace();
		}
		message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		return message.getMap();
	}
	
	/**
	 * 删除转发
	 * @return
	 */
	@RequestMapping(value = "/transmit", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> delete(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		try {
			if(!checkParams(message, request))
				return message.getMap();
			
			message.putAll(transmitService.deleteTransmit(getJsonFromMessage(message), getUserFromMessage(message), request));
			return message.getMap();
		} catch (Exception e) {
			e.printStackTrace();
		}
		message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		return message.getMap();
	}
	
	/**
	 * 获取转发列表
	 * @return
	 */
	@RequestMapping(value="/transmits", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> paging(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		try {
			checkParams(message, request);
			
			List<Map<String, Object>> result= transmitService.getLimit(getJsonFromMessage(message), getUserFromMessage(message), request);
			System.out.println("获得转发的数量：" +result.size());
			message.put("isSuccess", true);
			message.put("message", result);
			return message.getMap();
		} catch (Exception e) {
			e.printStackTrace();
		}
		message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		return message.getMap();
	}

	/**
	 * 更改转发编辑状态
	 * {'can_transmit':true, 'table_name':'t_mood', 'table_id': 1},所有参数全部必须
	 * @return
	 */
	@RequestMapping(value = "/transmit", method = RequestMethod.PUT, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> updateTransmitStatus(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		try {
			if(!checkParams(message, request))
				return message.getMap();
			
			message.putAll(transmitService.updateTransmitStatus(getJsonFromMessage(message), getUserFromMessage(message), request));
			return message.getMap();
		} catch (Exception e) {
			e.printStackTrace();
		}
		message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		return message.getMap();
	}
}
