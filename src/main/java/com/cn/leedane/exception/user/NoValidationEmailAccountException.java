package com.cn.leedane.exception.user;

import org.apache.shiro.authc.AccountException;

/**
 * 用户未验证邮箱异常
 * @author LeeDane
 * 2017年3月28日 上午11:27:42
 * version 1.0
 */
public class NoValidationEmailAccountException extends AccountException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 500191729804519210L;

	/**
     * Creates a new NoValidationEmailAccountException.
     */
    public NoValidationEmailAccountException() {
        super();
    }

    /**
     * Constructs a new NoValidationEmailAccountException.
     *
     * @param message the reason for the exception
     */
    public NoValidationEmailAccountException(String message) {
        super(message);
    }

    /**
     * Constructs a new NoValidationEmailAccountException.
     *
     * @param cause the underlying Throwable that caused this exception to be thrown.
     */
    public NoValidationEmailAccountException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new UnknownAccountException.
     *
     * @param message the reason for the exception
     * @param cause   the underlying Throwable that caused this exception to be thrown.
     */
    public NoValidationEmailAccountException(String message, Throwable cause) {
        super(message, cause);
    }
}
