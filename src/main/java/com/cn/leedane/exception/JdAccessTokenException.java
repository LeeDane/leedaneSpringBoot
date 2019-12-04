package com.cn.leedane.exception;


/**
 * 获取京东access_token的异常
 * @author LeeDane
 * 2019年12月10日 下午4:46:46
 * version 1.0
 */
public class JdAccessTokenException extends RuntimeException{
	/**
	 *
	 */
	private static final long serialVersionUID = -6516590243681290094L;

	/**
     * Creates a new JdAccessTokenException.
     */
    public JdAccessTokenException() {
        super();
    }

    /**
     * Constructs a new JdAccessTokenException.
     *
     * @param message the reason for the exception
     */
    public JdAccessTokenException(String message) {
        super(message);
    }

    /**
     * Constructs a new JdAccessTokenException.
     *
     * @param cause the underlying Throwable that caused this exception to be thrown.
     */
    public JdAccessTokenException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new UnknownAccountException.
     *
     * @param message the reason for the exception
     * @param cause   the underlying Throwable that caused this exception to be thrown.
     */
    public JdAccessTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
