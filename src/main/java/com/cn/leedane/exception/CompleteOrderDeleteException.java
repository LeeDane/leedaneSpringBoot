package com.cn.leedane.exception;


/**
 * 已经完成的状态的订单的删除异常
 * @author LeeDane
 * 2017年12月10日 下午4:46:46
 * version 1.0
 */
public class CompleteOrderDeleteException extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6516590243681290094L;

	/**
     * Creates a new CompleteOrderDeleteException.
     */
    public CompleteOrderDeleteException() {
        super();
    }

    /**
     * Constructs a new CompleteOrderDeleteException.
     *
     * @param message the reason for the exception
     */
    public CompleteOrderDeleteException(String message) {
        super(message);
    }

    /**
     * Constructs a new CompleteOrderDeleteException.
     *
     * @param cause the underlying Throwable that caused this exception to be thrown.
     */
    public CompleteOrderDeleteException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new UnknownAccountException.
     *
     * @param message the reason for the exception
     * @param cause   the underlying Throwable that caused this exception to be thrown.
     */
    public CompleteOrderDeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}
