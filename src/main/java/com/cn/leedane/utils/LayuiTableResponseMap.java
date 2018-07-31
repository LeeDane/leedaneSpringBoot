package com.cn.leedane.utils;

import java.util.List;
import java.util.Map;
/**
 * 请求响应的Map
 * @author LeeDane
 * 2017年3月19日 下午10:27:49
 * Version 1.0
 */
public class LayuiTableResponseMap extends ResponseMap{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LayuiTableResponseMap(int code, String msg, int count, List<Map<String, Object>> data){
		put("code", code);
		put("msg", msg);
		put("count", count);
		put("data", data);
	}
	
	public LayuiTableResponseMap(){
		
	}
	
	public void setCode(int code) {
		put("code", code);
	}

	public void setMsg(String msg) {
		put("msg", msg);
	}

	public void setCount(int count) {
		put("count", count);
	}

	public void setData(List<Map<String, Object>> data) {
		put("data", data);
	}
	
}
