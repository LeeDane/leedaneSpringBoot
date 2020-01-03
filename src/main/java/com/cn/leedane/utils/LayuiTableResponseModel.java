package com.cn.leedane.utils;

import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

/**
 * 请求响应的Map
 * @author LeeDane
 * 2017年3月19日 下午10:27:49
 * Version 1.0
 */
public class LayuiTableResponseModel extends ResponseModel{
	private String msg;
	private List<Map<String, Object>> data;
	private long count;
	public LayuiTableResponseModel(int code, String msg, long count, List<Map<String, Object>> data){
		code(code);
		setMsg(msg);
		setCount(count);
		setData(data);
	}

	public LayuiTableResponseModel(){

	}

	public String getMsg() {
		return msg;
	}

	public LayuiTableResponseModel setMsg(String msg) {
		this.msg = msg;
		return LayuiTableResponseModel.this;
	}

	public List<Map<String, Object>> getData() {
		return data;
	}

	public LayuiTableResponseModel setData(List<Map<String, Object>> data) {
		this.data = data;
		return LayuiTableResponseModel.this;
	}

	public long getCount() {
		return count;
	}

	public LayuiTableResponseModel setCount(long count) {
		this.count = count;
		return LayuiTableResponseModel.this;
	}
}
