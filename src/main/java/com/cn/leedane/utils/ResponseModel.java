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
public class ResponseModel{
	private Logger logger = Logger.getLogger(getClass());

	private boolean isSuccess;
	private long start;
	private String consumeTime;
	private int code;
	private long total;
	private Object message;

	private Map<String, Object> extra;

	public ResponseModel(){
		start = System.currentTimeMillis();
		isSuccess = false;
	}

	public ResponseModel start(long start){
		this.start = start;
		return ResponseModel.this;
	}
	public boolean isSuccess() {
		return isSuccess;
	}

	public ResponseModel error() {
		isSuccess = false;
		if(code < 1)
			this.code = EnumUtil.ResponseCode.操作失败.value;
		return ResponseModel.this;
	}

	public ResponseModel ok() {
		isSuccess = true;
		if(code < 1)
			this.code = EnumUtil.ResponseCode.请求返回成功码.value;
		return ResponseModel.this;
	}

	public ResponseModel setConsumeTime(String consumeTime) {
		this.consumeTime = consumeTime;
		return ResponseModel.this;
	}

	public String getConsumeTime() {
		if(StringUtil.isNotNull(consumeTime))
			return consumeTime;
		if(start > 0){
			long end = System.currentTimeMillis();
			return (end - start) +"ms";
		}
		return "";
	}

	public Object getMessage() {
		return message;
	}

	public ResponseModel message(Object message) {
		this.message = message;
		return ResponseModel.this;
	}

	public ResponseModel code(int code) {
		this.code = code;
		return ResponseModel.this;
	}

	public ResponseModel total(long total) {
		this.total = total;
		return ResponseModel.this;
	}

	public int getCode() {
		return code;
	}

	public long getTotal() {
		return total;
	}

	public Map<String, Object> getExtra() {
		return extra;
	}

	public ResponseModel extra(Map<String, Object> extra) {
		this.extra = extra;
		return ResponseModel.this;
	}

	/*public HashMap<String, Object> getMap(){
		if(containsKey("json"))
			remove("json");
		
		if(containsKey("user"))
			remove("user");

		logger.info("服务器返回:"+ JSONObject.fromObject(this).toString());

		if(start > 0){
			long end = System.currentTimeMillis();
			this.put("consumeTime", );
		}

		return this;
	}*/

}
