package com.cn.leedane.mall.github;

/**
 * 自定义github平台异常处理
 * @author LeeDane
 * 2019年12月09日 12:01
 * Version 1.0
 */
public class GithubException extends Exception {

    private static final long serialVersionUID = -7035498848577048685L;
    private String errCode;
    private String errMsg;

    public GithubException() {
    }

    public GithubException(String message, Throwable cause) {
        super(message, cause);
    }

    public GithubException(String message) {
        super(message);
    }

    public GithubException(Throwable cause) {
        super(cause);
    }

    public GithubException(String errCode, String errMsg) {
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
