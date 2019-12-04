package com.cn.leedane.exception.user;

import org.apache.shiro.authc.AccountException;

/**
 * 用户被禁言异常
 * @author LeeDane
 * 2017年3月28日 上午11:20:16
 * version 1.0
 */
public class BannedAccountException extends AccountException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Creates a new BannedAccountException.
     */
    public BannedAccountException() {
        super();
    }

    /**
     * Constructs a new BannedAccountException.
     *
     * @param message the reason for the exception
     */
    public BannedAccountException(String message) {
        super(message);
    }

    /**
     * Constructs a new BannedAccountException.
     *
     * @param cause the underlying Throwable that caused this exception to be thrown.
     */
    public BannedAccountException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new UnknownAccountException.
     *
     * @param message the reason for the exception
     * @param cause   the underlying Throwable that caused this exception to be thrown.
     */
    public BannedAccountException(String message, Throwable cause) {
        super(message, cause);
    }
}
