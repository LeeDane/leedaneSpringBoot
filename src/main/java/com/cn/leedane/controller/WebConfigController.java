package com.cn.leedane.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cn.leedane.utils.ControllerBaseNameUtil;
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
	
	@Autowired
	private WebBackground webBackground;
	
	/**
	 * post请求方法名称
	 */
	public static final String REQUEST_METHOD_POST = "POST";
	
	@RequestMapping(value= "/background", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> background(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		
		message.put("message", webBackground.getImage());
		message.put("success", true);
		return message.getMap();
	}
	
	/*private void closePrintWriter(PrintWriter out){
		if(out != null){
			out.close();
		}
	}*/
}
