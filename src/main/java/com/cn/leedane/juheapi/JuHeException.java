package com.cn.leedane.juheapi;

/**
 * 聚合api平台异常处理
 * @author LeeDane
 * 2019年12月09日 12:01
 * Version 1.0
 */
public class JuHeException extends Exception {

    private static final long serialVersionUID = -7035498848577048685L;
    private String errCode;
    private String errMsg;

    public JuHeException() {
    }

    public JuHeException(String message, Throwable cause) {
        super(message, cause);
    }

    public JuHeException(String message) {
        super(message);
    }

    public JuHeException(Throwable cause) {
        super(cause);
    }

    public JuHeException(String errCode, String errMsg) {
        super(errCode + ": " + errMsg);
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    public String getErrCode() {
        return this.errCode;
    }

    public String getErrMsg() {
        return this.errMsg;
    }
}
