package com.cn.leedane.utils;

import java.util.HashMap;

import net.sf.json.JSONObject;

/**
 * 
 * @author T5-SK
 *
 */
/**
 * 请求响应的Map
 * @author LeeDane
 * 2017年3月19日 下午10:27:49
 * Version 1.0
 */
public class ResponseMap extends HashMap<String, Object>{

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
		System.out.println("服务器返回:"+ JSONObject.fromObject(this).toString());
		return this;
	}

}
