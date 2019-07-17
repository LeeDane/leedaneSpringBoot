package com.cn.leedane.exception;

/**
 * Elasticsearch连接出现异常
 * @author LeeDane
 * 2018年5月31日 上午10:31:20
 * version 1.0
 */
public class ElasticsearchConnectException extends RuntimeException{

	/**
	 *
	 */
	private static final long serialVersionUID = -7340728332812097791L;

	/**
     * Creates a new ElasticsearchConnectException.
     */
    public ElasticsearchConnectException() {
        super();
    }

    /**
     * Constructs a new ElasticsearchConnectException.
     *
     * @param message the reason for the exception
     */
    public ElasticsearchConnectException(String message) {
        super(message);
    }

    /**
     * Constructs a new ElasticsearchConnectException.
     *
     * @param cause the underlying Throwable that caused this exception to be thrown.
     */
    public ElasticsearchConnectException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new ElasticsearchConnectException.
     *
     * @param message the reason for the exception
     * @param cause   the underlying Throwable that caused this exception to be thrown.
     */
    public ElasticsearchConnectException(String message, Throwable cause) {
        super(message, cause);
    }

}
