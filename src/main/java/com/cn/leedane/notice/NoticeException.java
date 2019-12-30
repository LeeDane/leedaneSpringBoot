package com.cn.leedane.notice;

/**
 * 自定义通知异常处理
 * @author LeeDane
 * 2019年12月09日 12:01
 * Version 1.0
 */
public class NoticeException extends Exception {

    private static final long serialVersionUID = -7035498848577048685L;
    private String errCode;
    private String errMsg;

    public NoticeException() {
    }

    public NoticeException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoticeException(String message) {
        super(message);
    }

    public NoticeException(Throwable cause) {
        super(cause);
    }

    public NoticeException(String errCode, String errMsg) {
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
