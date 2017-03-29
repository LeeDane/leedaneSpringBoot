package com.cn.leedane.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.ResponseMap;
import com.cn.leedane.utils.WebBackground;

/**
 * 网络配置信息获取
 * @author LeeDane
 * 2016年10月19日 上午10:30:49
 * Version 1.0
 */
@RestController
@RequestMapping(value = ControllerBaseNameUtil.wc)
public class WebConfigController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());
	/**
	 * get请求方法名称
	 */
	public static final String REQUEST_METHOD_GET = "GET";
	
	/**
	 * post请求方法名称
	 */
	public static final String REQUEST_METHOD_POST = "POST";
	
	@RequestMapping(value= "/background", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> background(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		try {
			checkParams(message, request);
			
			message.put("message", new WebBackground().getImage());
			message.put("isSuccess", true);
			return message.getMap();
		} catch (Exception e) {
			e.printStackTrace();
		}     
        message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
		message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		return message.getMap();
	}
	
	/*private void closePrintWriter(PrintWriter out){
		if(out != null){
			out.close();
		}
	}*/
}
