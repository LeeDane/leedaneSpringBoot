package com.cn.leedane.exception.user;

import org.apache.shiro.authc.AccountException;

/**
 * 用户未完善信息异常
 * @author LeeDane
 * 2017年3月28日 上午11:32:34
 * version 1.0
 */
public class NoCompleteAccountException extends AccountException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7381003080575037668L;

	/**
     * Creates a new NoCompleteAccountException.
     */
    public NoCompleteAccountException() {
        super();
    }

    /**
     * Constructs a new NoCompleteAccountException.
     *
     * @param message the reason for the exception
     */
    public NoCompleteAccountException(String message) {
        super(message);
    }

    /**
     * Constructs a new NoCompleteAccountException.
     *
     * @param cause the underlying Throwable that caused this exception to be thrown.
     */
    public NoCompleteAccountException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new UnknownAccountException.
     *
     * @param message the reason for the exception
     * @param cause   the underlying Throwable that caused this exception to be thrown.
     */
    public NoCompleteAccountException(String message, Throwable cause) {
        super(message, cause);
    }
}
