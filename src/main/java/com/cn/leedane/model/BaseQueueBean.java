package com.cn.leedane.model;

import java.io.Serializable;

/**
 * 基本的队列bean
 * @author LeeDane
 * 2018年11月26日 下午3:52:39
 * version 1.0
 */
public abstract class BaseQueueBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 失败的次数
	 */
	private int error;
	public int getError() {
		return error;
	}
	public void setError(int error) {
		this.error = error;
	}

}
