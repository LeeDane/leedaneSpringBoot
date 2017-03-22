package com.cn.leedane.exception;

/**
 * 不支持的参数异常
 * 为什么需要自定义的异常类：因为有时候会调用多个有checked异常的方法，这样会导致上一层的代码在实现的时候会
 * 填写大量的代码来捕获相关的异常，要是这些异常有个共性，就可以通过自定义异常的方式减少相关的代码。
 * @author LeeDane
 * 2016年7月12日 上午10:16:52
 * Version 1.0
 */
public class UnsupportException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public UnsupportException(String message) {
		super(message);
	}

}
