package com.cn.leedane.exception.user;

import org.apache.shiro.authc.AccountException;

/**
 * 用户未激活异常
 * @author LeeDane
 * 2017年3月28日 上午11:29:42
 * version 1.0
 */
public class NoActiveAccountException extends AccountException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6325525210817068533L;

	/**
     * Creates a new NoActiveAccountException.
     */
    public NoActiveAccountException() {
        super();
    }

    /**
     * Constructs a new NoActiveAccountException.
     *
     * @param message the reason for the exception
     */
    public NoActiveAccountException(String message) {
        super(message);
    }

    /**
     * Constructs a new NoActiveAccountException.
     *
     * @param cause the underlying Throwable that caused this exception to be thrown.
     */
    public NoActiveAccountException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new UnknownAccountException.
     *
     * @param message the reason for the exception
     * @param cause   the underlying Throwable that caused this exception to be thrown.
     */
    public NoActiveAccountException(String message, Throwable cause) {
        super(message, cause);
    }
}
