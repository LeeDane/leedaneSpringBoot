package com.cn.leedane.model;

import java.io.Serializable;


/**
 * 服务器本地文件实体类
 * @author LeeDane
 * 2016年12月1日 上午11:56:17
 * Version 1.0
 */
public class ServerFileBean implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * 路径
	 */
	private String path;
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}
