package com.cn.leedane.exception;

/**
 * 运行404异常
 * @author LeeDane
 * 2017年4月12日 上午11:01:32
 * version 1.0
 */
public class RE404Exception extends RuntimeException{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7340728332812097791L;

	/**
     * Creates a new RE404Exception.
     */
    public RE404Exception() {
        super();
    }

    /**
     * Constructs a new RE404Exception.
     *
     * @param message the reason for the exception
     */
    public RE404Exception(String message) {
        super(message);
    }

    /**
     * Constructs a new RE404Exception.
     *
     * @param cause the underlying Throwable that caused this exception to be thrown.
     */
    public RE404Exception(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new RE404Exception.
     *
     * @param message the reason for the exception
     * @param cause   the underlying Throwable that caused this exception to be thrown.
     */
    public RE404Exception(String message, Throwable cause) {
        super(message, cause);
    }

}
