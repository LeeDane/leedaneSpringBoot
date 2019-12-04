package com.cn.leedane.display;


/**
 * key-value格式展示的处理
 * @author LeeDane
 * 2018年6月5日 下午6:09:54
 * version 1.0
 */
public class keyValueDisplay{
	
	private String key;
	
	private String value;
	
	public keyValueDisplay(String key, String value){
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
}
