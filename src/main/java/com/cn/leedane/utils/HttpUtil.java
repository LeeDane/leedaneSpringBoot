package com.cn.leedane.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

/**
 * 网络相关的工具类
 * @author LeeDane
 * 2016年7月12日 上午10:29:03
 * Version 1.0
 */
public class HttpUtil{

	public HttpUtil() {
	}
	
	/**
	 * 将HttpServletRequest对象转成jSONObject格式的数据
	 * @param request  http请求
	 * @return
	 * @throws Exception
	 */
	public static JSONObject getJsonObjectFromInputStream(HttpServletRequest request) throws Exception {
			return getJsonObjectFromInputStream(null,request);
	}
	
	/**
	 * 将HttpServletRequest对象转成jSONObject格式的数据
	 * @param result 结果
	 * @param request  http请求
	 * @return
	 * @throws Exception
	 */
	public static JSONObject getJsonObjectFromInputStream(String result,HttpServletRequest request) throws Exception {
		if(result == null){
			InputStream inp = request.getInputStream();
			return JsonUtil.getInstance().toJsonObject(inp);
		}else
			return JSONObject.fromObject(result); 
	}
	/**
	 * Servlet请求转发
	 * @param request http请求
	 * @param response   http响应
	 * @param url  跳转的url
	 * @throws ServletException
	 * @throws IOException
	 */
	public static void dispatch(HttpServletRequest request, HttpServletResponse response, String url) throws ServletException, IOException{
		request.getRequestDispatcher(url).forward(request, response);
	}
	
	/**
	 * 输出json数据
	 * @param response  http响应
	 * @param json json对象
	 */
	public static void sendJson(HttpServletResponse response,Object json){
		PrintWriter out=null;
		try{
			response.setCharacterEncoding("utf-8");
			response.setContentType("application/x-json");
			out = response.getWriter(); 		
			out.print(json.toString());				
			out.flush();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			out.close();
		}
	}
	
	/**
	 * 获取网络图片的InputStream流
	 * @param imgUrl
	 * @return
	 */
	public static InputStream getInputStream(String imgUrl){
		if(StringUtil.isNull(imgUrl))
			return null;
		
		URL url = null;
		try {
			url = new URL(imgUrl);
			URLConnection uc = url.openConnection(); 
			uc.setConnectTimeout(60000);
			uc.setDoInput(true);
			uc.setDoInput(true);
			uc.setReadTimeout(30000);
			return uc.getInputStream(); 
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return null;
	}
}
