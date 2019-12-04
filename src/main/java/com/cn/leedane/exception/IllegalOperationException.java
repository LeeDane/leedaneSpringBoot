package com.cn.leedane.exception;


/**
 * 非法操作异常
 * @author LeeDane
 * 2018年10月26日 下午5:16:39
 * version 1.0
 */
public class IllegalOperationException extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6516590243681290094L;

	/**
     * Creates a new IllegalOperationException.
     */
    public IllegalOperationException() {
        super();
    }

    /**
     * Constructs a new IllegalOperationException.
     *
     * @param message the reason for the exception
     */
    public IllegalOperationException(String message) {
        super(message);
    }

    /**
     * Constructs a new IllegalOperationException.
     *
     * @param cause the underlying Throwable that caused this exception to be thrown.
     */
    public IllegalOperationException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new UnknownAccountException.
     *
     * @param message the reason for the exception
     * @param cause   the underlying Throwable that caused this exception to be thrown.
     */
    public IllegalOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
