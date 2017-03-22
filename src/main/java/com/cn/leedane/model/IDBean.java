package com.cn.leedane.model;

import java.io.Serializable;

import org.apache.solr.client.solrj.beans.Field;


/**
 * 自增长的 ID类的基类
 * @author LeeDane
 * 2016年7月12日 上午10:12:29
 * Version 1.0
 */
//@MappedSuperclass
public class IDBean implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4775876471761087890L;
	
	/**
	 * 基本的ID列
	 */
	@Field
	protected int id;
	
	//@Id
    //@GeneratedValue(strategy=GenerationType.AUTO)
	public int getId() {
		return id;
	}
	
	//@Field
	public void setId(int id) {
		this.id = id;
	}
}

