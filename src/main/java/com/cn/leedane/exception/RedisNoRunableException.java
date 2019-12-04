package com.cn.leedane.exception;

import java.net.ConnectException;

/**
 * Redis没有启动的异常
 * @author LeeDane
 * 2017年2月27日 下午5:31:31
 * Version 1.0
 */
public class RedisNoRunableException extends ConnectException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public RedisNoRunableException(String message){
		super(message);
	}

}
