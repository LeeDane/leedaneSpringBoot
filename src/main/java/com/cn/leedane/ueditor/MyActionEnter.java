package com.cn.leedane.ueditor;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import com.baidu.ueditor.ConfigManager;
import com.baidu.ueditor.define.ActionMap;
import com.baidu.ueditor.define.BaseState;
import com.baidu.ueditor.define.State;
import com.baidu.ueditor.hunter.ImageHunter;
import com.cn.leedane.controller.UserController;
import com.cn.leedane.utils.EnumUtil;

/**
 * 重写百度富文本编辑器的ActionEnter类
 * @author LeeDane
 * 2016年12月1日 上午11:06:41
 * Version 1.0
 */
public class MyActionEnter {
	private HttpServletRequest request = null;
	
	private String rootPath = null;
	private String contextPath = null;
	 
	private String actionType = null;
	
	private ConfigManager configManager = null;
	
	private boolean isLogin;
	
	public MyActionEnter(HttpServletRequest request, String rootPath){
		this.request = request;
		this.rootPath = rootPath;
		this.actionType = request.getParameter("action");
		this.contextPath = request.getContextPath();
		this.configManager = ConfigManager.getInstance(this.rootPath, this.contextPath, request.getRequestURI());
		isLogin = request.getSession().getAttribute(UserController.USER_INFO_KEY) != null;
	}
	 
	public String exec(){
		if(!isLogin){
			String error = "{\"success\": false, \"state\":\""+ EnumUtil.getResponseValue(EnumUtil.ResponseCode.请先登录.value) +"\"}";
			JSONObject jsonObject = JSONObject.fromObject(error);
			return jsonObject.toString();
		}
		
		String callbackName = this.request.getParameter("callback");
		if (callbackName != null){
			if (!validCallbackName(callbackName)) {
				return new BaseState(false, 401).toJSONString();
			}
			return callbackName + "(" + invoke() + ");";
		}
		return invoke();
	}
	public String invoke(){
		if ((this.actionType == null) || (!ActionMap.mapping.containsKey(this.actionType))) {
			return new BaseState(false, 101).toJSONString();
		}
		if ((this.configManager == null) || (!this.configManager.valid())) {
			return new BaseState(false, 102).toJSONString();
		}

		State state = null;
		int actionCode = ActionMap.getType(this.actionType);
		Map conf = null;
		switch (actionCode){
			case 0:
				return this.configManager.getAllConfig().toString();
			case 1:
			case 2:
			case 3:
			case 4:
				conf = this.configManager.getConfig(actionCode);
				state = new MyUploader(this.request, conf).doExec();
				break;
			case 5:
				conf = this.configManager.getConfig(actionCode);
				String[] list = this.request.getParameterValues((String)conf.get("fieldName"));
				state = new ImageHunter(conf).capture(list);
				break;
			case 6:
			case 7:
				conf = this.configManager.getConfig(actionCode);
				int start = getStartIndex();
				state = new MyFileManager(conf).listFile(start);
		}
		return state.toJSONString();
	}

	public int getStartIndex(){
		String start = this.request.getParameter("start");
		try{
			return Integer.parseInt(start); 
		} catch (Exception e){}
		return 0;
	}
	
	public boolean validCallbackName(String name){
		if (name.matches("^[a-zA-Z_]+[\\w0-9_]*$")) {
			return true;
		}
		return false;
	}
}
