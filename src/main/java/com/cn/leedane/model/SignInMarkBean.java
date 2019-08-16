package com.cn.leedane.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


/**
 * 全部签到标记实体类
 * @author LeeDane
 * 2019年8月19日 下午19:37:24
 * Version 1.0
 */
public class SignInMarkBean implements Serializable {
	
	private List<Map<String, Object>> list;

	public List<Map<String, Object>> getList() {
		return list;
	}

	public void setList(List<Map<String, Object>> list) {
		this.list = list;
	}
}
