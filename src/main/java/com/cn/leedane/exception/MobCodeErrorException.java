package com.cn.leedane.exception;

/**
 * 短信验证码验证失败异常
 * @author LeeDane
 * 2017年7月12日 下午5:56:26
 * version 1.0
 */
public class MobCodeErrorException extends RuntimeException{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7340728332812097791L;

	/**
     * Creates a new MobCodeErrorException.
     */
    public MobCodeErrorException() {
        super();
    }

    /**
     * Constructs a new MobCodeErrorException.
     *
     * @param message the reason for the exception
     */
    public MobCodeErrorException(String message) {
        super(message);
    }

    /**
     * Constructs a new MobCodeErrorException.
     *
     * @param cause the underlying Throwable that caused this exception to be thrown.
     */
    public MobCodeErrorException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new MobCodeErrorException.
     *
     * @param message the reason for the exception
     * @param cause   the underlying Throwable that caused this exception to be thrown.
     */
    public MobCodeErrorException(String message, Throwable cause) {
        super(message, cause);
    }

}
