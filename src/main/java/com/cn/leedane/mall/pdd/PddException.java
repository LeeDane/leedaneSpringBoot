package com.cn.leedane.mall.pdd;

/**
 * 自定义拼多多平台异常处理
 * @author LeeDane
 * 2019年12月09日 12:01
 * Version 1.0
 */
public class PddException extends Exception {

    private static final long serialVersionUID = -7035498848577048685L;
    private String errCode;
    private String errMsg;

    public PddException() {
    }

    public PddException(String message, Throwable cause) {
        super(message, cause);
    }

    public PddException(String message) {
        super(message);
    }

    public PddException(Throwable cause) {
        super(cause);
    }

    public PddException(String errCode, String errMsg) {
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
