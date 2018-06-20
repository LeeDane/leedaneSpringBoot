package com.cn.leedane.exception;

/**
 * 必须是登录的管理员账号运行时异常
 * @author LeeDane
 * 2018年6月6日 下午3:39:34
 * version 1.0
 */
public class MustAdminLoginException extends RuntimeException{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7340728332812097791L;

	/**
     * Creates a new MustAdminLoginException.
     */
    public MustAdminLoginException() {
        super();
    }

    /**
     * Constructs a new MustAdminLoginException.
     *
     * @param message the reason for the exception
     */
    public MustAdminLoginException(String message) {
        super(message);
    }

    /**
     * Constructs a new MustAdminLoginException.
     *
     * @param cause the underlying Throwable that caused this exception to be thrown.
     */
    public MustAdminLoginException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new MustAdminLoginException.
     *
     * @param message the reason for the exception
     * @param cause   the underlying Throwable that caused this exception to be thrown.
     */
    public MustAdminLoginException(String message, Throwable cause) {
        super(message, cause);
    }

}
