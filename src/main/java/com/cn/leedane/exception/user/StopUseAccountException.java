package com.cn.leedane.exception.user;

import org.apache.shiro.authc.AccountException;

/**
 * 用户暂时被禁止使用异常
 * @author LeeDane
 * 2017年3月28日 上午11:25:25
 * version 1.0
 */
public class StopUseAccountException extends AccountException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7632105329455375155L;

	/**
     * Creates a new StopUseAccountException.
     */
    public StopUseAccountException() {
        super();
    }

    /**
     * Constructs a new StopUseAccountException.
     *
     * @param message the reason for the exception
     */
    public StopUseAccountException(String message) {
        super(message);
    }

    /**
     * Constructs a new StopUseAccountException.
     *
     * @param cause the underlying Throwable that caused this exception to be thrown.
     */
    public StopUseAccountException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new UnknownAccountException.
     *
     * @param message the reason for the exception
     * @param cause   the underlying Throwable that caused this exception to be thrown.
     */
    public StopUseAccountException(String message, Throwable cause) {
        super(message, cause);
    }
}
