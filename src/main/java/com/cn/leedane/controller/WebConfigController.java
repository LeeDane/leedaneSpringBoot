package com.cn.leedane.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cn.leedane.utils.WebBackground;

/**
 * 网络配置信息获取
 * @author LeeDane
 * 2016年10月19日 上午10:30:49
 * Version 1.0
 */
@RestController
@RequestMapping("wc")
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
	
	@RequestMapping(value= "/background", method = RequestMethod.GET)
	public String background(HttpServletRequest request, HttpServletResponse response){
		/*PrintWriter out = null;
		try {
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");
			out = response.getWriter();
		} catch (IOException e ) {
			e.printStackTrace();
		}
		try{
			out.print(new WebBackground().getImage());
		}catch(Exception e){
			e.printStackTrace();
			closePrintWriter(out);
		}finally{
			closePrintWriter(out);
		}
		*/
		return new WebBackground().getImage();
	}
	
	/*private void closePrintWriter(PrintWriter out){
		if(out != null){
			out.close();
		}
	}*/
}
