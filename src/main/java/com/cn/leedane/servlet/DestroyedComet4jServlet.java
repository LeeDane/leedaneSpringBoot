package com.cn.leedane.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cn.leedane.comet4j.AppStore;
import com.cn.leedane.comet4j.Comet4jServer;
import com.cn.leedane.utils.StringUtil;


/**
 * 销毁长连接
 * @author LeeDane
 * 2016年11月28日 下午5:49:14
 * Version 1.0
 */
public class DestroyedComet4jServlet extends HttpServlet{

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
		String key = (String) request.getParameter("cid");
		String channel = request.getParameter("channel");
		
		if(StringUtil.isNotNull(channel) && Comet4jServer.SCAN_LOGIN.equals(channel)){
			AppStore.getInstance().removeScanLoginKey(key);
			System.out.println("cid为"+key+"已经销毁，还剩余链接数："+AppStore.getInstance().getScanLogin().size());
		}
	}
	
	public void destroy() {
	}
	
}
