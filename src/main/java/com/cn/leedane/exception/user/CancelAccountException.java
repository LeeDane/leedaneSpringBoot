package com.cn.leedane.exception.user;

import org.apache.shiro.authc.AccountException;

/**
 * 用户已经注销异常
 * @author LeeDane
 * 2017年3月28日 上午11:23:38
 * version 1.0
 */
public class CancelAccountException extends AccountException{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6516590243681290094L;

	/**
     * Creates a new CancelAccountException.
     */
    public CancelAccountException() {
        super();
    }

    /**
     * Constructs a new CancelAccountException.
     *
     * @param message the reason for the exception
     */
    public CancelAccountException(String message) {
        super(message);
    }

    /**
     * Constructs a new CancelAccountException.
     *
     * @param cause the underlying Throwable that caused this exception to be thrown.
     */
    public CancelAccountException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new UnknownAccountException.
     *
     * @param message the reason for the exception
     * @param cause   the underlying Throwable that caused this exception to be thrown.
     */
    public CancelAccountException(String message, Throwable cause) {
        super(message, cause);
    }
}
