package com.cn.leedane.exception;

/**
 * 操作运行时异常
 * @author LeeDane
 * 2017年4月21日 下午1:15:28
 * Version 1.0
 */
public class OperateException extends RuntimeException{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7340728332812097791L;

	/**
     * Creates a new OperateException.
     */
    public OperateException() {
        super();
    }

    /**
     * Constructs a new OperateException.
     *
     * @param message the reason for the exception
     */
    public OperateException(String message) {
        super(message);
    }

    /**
     * Constructs a new OperateException.
     *
     * @param cause the underlying Throwable that caused this exception to be thrown.
     */
    public OperateException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new OperateException.
     *
     * @param message the reason for the exception
     * @param cause   the underlying Throwable that caused this exception to be thrown.
     */
    public OperateException(String message, Throwable cause) {
        super(message, cause);
    }

}
