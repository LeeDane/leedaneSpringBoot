package com.cn.leedane.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cn.leedane.utils.HttpUtil;

/**
 * Restful接口风格(不是标准)的api接口
 * @author LeeDane
 * 2017年2月25日 下午12:01:52
 * Version 1.0
 */
@Controller
@RequestMapping(value="/leedane/api")
public class RestfulApiController extends BaseController{

	@RequestMapping(value="/users", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public String getUser(HttpServletRequest request, HttpServletResponse response){
		String id = request.getParameter("id");
		System.out.println("helloa"+id);
		return null;
	}
	
	@RequestMapping(value="/users", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public String addUser(HttpServletRequest request, HttpServletResponse response){
		String id = request.getParameter("id");
		try {
			JSONObject json = HttpUtil.getJsonObjectFromInputStream(request);
			if(json != null)
				System.out.println(json.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("helloee"+id);
		return null;
	}
	
	@RequestMapping(value="/users", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"})
	public String deleteUser(HttpServletRequest request, HttpServletResponse response){
		String id = request.getParameter("id");
		try {
			JSONObject json = HttpUtil.getJsonObjectFromInputStream(request);
			if(json != null)
				System.out.println(json.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("hello哈哈"+id);
		Map<String, Object> map = new HashMap<String, Object>();
		return JSONObject.fromObject(map).toString();
	}
}
