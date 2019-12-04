package com.cn.leedane.exception;

/**
 * 必须登录运行时异常
 * @author LeeDane
 * 2018年6月6日 下午3:03:42
 * version 1.0
 */
public class MustLoginException extends RuntimeException{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7340728332812097791L;

	/**
     * Creates a new MustLoginException.
     */
    public MustLoginException() {
        super();
    }

    /**
     * Constructs a new MustLoginException.
     *
     * @param message the reason for the exception
     */
    public MustLoginException(String message) {
        super(message);
    }

    /**
     * Constructs a new MustLoginException.
     *
     * @param cause the underlying Throwable that caused this exception to be thrown.
     */
    public MustLoginException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new MustLoginException.
     *
     * @param message the reason for the exception
     * @param cause   the underlying Throwable that caused this exception to be thrown.
     */
    public MustLoginException(String message, Throwable cause) {
        super(message, cause);
    }

}
