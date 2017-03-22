package com.cn.leedane.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import com.cn.leedane.comet4j.AppStore;
import com.cn.leedane.handler.ZXingCodeHandler;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.StringUtil;
import com.google.zxing.WriterException;


/**
 * 获取登录二维码
 * @author LeeDane
 * 2016年11月28日 下午6:02:43
 * Version 1.0
 */
public class LoginQrCode extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	public void init() throws ServletException {
	}

	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String cid = request.getParameter("cid");//连接ID，必须 
		Map<String, Object> message = new HashMap<String, Object>();
		message.put("isSuccess", false);
		if(StringUtil.isNull(cid)){
			message.put("message", "连接id为空");
			message.put("responseCode", EnumUtil.ResponseCode.参数不存在或为空.value);
			printWriter(message, response);
			return;
		}
		
		if(AppStore.getInstance().getScanLogin(cid) == null){
			message.put("message", "连接对象不存在，请重新刷新当前页");
			message.put("responseCode", EnumUtil.ResponseCode.参数不存在或为空.value);
			printWriter(message, response);
			return;
		}
		try {
			String bp = request.getScheme()+"://"+request.getServerName() +
					(request.getServerName().endsWith("com")? "" : ":"+request.getServerPort())
					+request.getContextPath()+"/";
			String bpath = bp + "page/download.jsp?scan_login=" + cid;
			message.put("message", ZXingCodeHandler.createQRCode(bpath, 200));
			message.put("isSuccess", true);
		} catch (WriterException e) {
			message.put("message", e.toString());
			message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
		}
		printWriter(message, response);
	}
	
	public void destroy() {
	}
	
	
	/**
	 * 通过原先servlet方式输出json对象。
	 * 目的：解决复杂的文本中含有特殊的字符导致struts2的json
	 * 		解析失败，给客户端返回500的bug
	 */
	protected void printWriter(Map<String, Object> message, HttpServletResponse response){
		if(message.containsKey("json"))
			message.remove("json");
		
		if(message.containsKey("user"))
			message.remove("user");
		
		JSONObject jsonObject = JSONObject.fromObject(message);
		response.setCharacterEncoding("utf-8");
		//System.out.println("服务器返回:"+jsonObject.toString());
		PrintWriter writer = null;
		try {
			writer = response.getWriter();
			writer.append(jsonObject.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(writer != null)
				writer.close();
		}
		
	}
}
