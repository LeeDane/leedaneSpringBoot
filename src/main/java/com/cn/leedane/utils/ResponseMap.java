package com.cn.leedane.utils;

import java.util.HashMap;

import org.apache.log4j.Logger;

import net.sf.json.JSONObject;

/**
 * 请求响应的Map
 * @author LeeDane
 * 2017年3月19日 下午10:27:49
 * Version 1.0
 */
public class ResponseMap extends HashMap<String, Object>{
	private Logger logger = Logger.getLogger(getClass());
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long start;
	
	public ResponseMap(){
		start = System.currentTimeMillis();
		put("isSuccess", false);
	}
	
	public HashMap<String, Object> getMap(){
		if(containsKey("json"))
			remove("json");
		
		if(containsKey("user"))
			remove("user");
		
		if(start > 0){
			long end = System.currentTimeMillis();
			this.put("consumeTime", (end - start) +"ms");
		}
		logger.info("服务器返回:"+ JSONObject.fromObject(this).toString());
		return this;
	}

}
