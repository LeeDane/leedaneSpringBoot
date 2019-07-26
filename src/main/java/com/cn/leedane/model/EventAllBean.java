package com.cn.leedane.model;

import org.apache.solr.client.solrj.beans.Field;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


/**
 * 全部大事件实体类
 * @author LeeDane
 * 2019年7月19日 下午19:37:24
 * Version 1.0
 */
public class EventAllBean implements Serializable {
	
	private List<Map<String, Object>> list;

	public List<Map<String, Object>> getList() {
		return list;
	}

	public void setList(List<Map<String, Object>> list) {
		this.list = list;
	}
}
