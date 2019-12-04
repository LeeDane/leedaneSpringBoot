package com.cn.leedane.exception;

/**
 * 测试角色运行时异常
 * @author LeeDane
 * 2018年5月31日 上午10:31:20
 * version 1.0
 */
public class TestRoleException extends RuntimeException{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7340728332812097791L;

	/**
     * Creates a new TestRoleException.
     */
    public TestRoleException() {
        super();
    }

    /**
     * Constructs a new TestRoleException.
     *
     * @param message the reason for the exception
     */
    public TestRoleException(String message) {
        super(message);
    }

    /**
     * Constructs a new TestRoleException.
     *
     * @param cause the underlying Throwable that caused this exception to be thrown.
     */
    public TestRoleException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new TestRoleException.
     *
     * @param message the reason for the exception
     * @param cause   the underlying Throwable that caused this exception to be thrown.
     */
    public TestRoleException(String message, Throwable cause) {
        super(message, cause);
    }

}
