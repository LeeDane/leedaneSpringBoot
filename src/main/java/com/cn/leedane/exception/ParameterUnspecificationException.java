package com.cn.leedane.exception;


/**
 * 参数不规范异常
 * @author LeeDane
 * 2017年12月8日 下午3:23:21
 * version 1.0
 */
public class ParameterUnspecificationException extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6516590243681290094L;

	/**
     * Creates a new ParameterUnspecificationException.
     */
    public ParameterUnspecificationException() {
        super();
    }

    /**
     * Constructs a new ParameterUnspecificationException.
     *
     * @param message the reason for the exception
     */
    public ParameterUnspecificationException(String message) {
        super(message);
    }

    /**
     * Constructs a new ParameterUnspecificationException.
     *
     * @param cause the underlying Throwable that caused this exception to be thrown.
     */
    public ParameterUnspecificationException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new UnknownAccountException.
     *
     * @param message the reason for the exception
     * @param cause   the underlying Throwable that caused this exception to be thrown.
     */
    public ParameterUnspecificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
