package com.cn.leedane.mall.jingdong;

import java.io.Serializable;
import org.codehaus.jackson.annotate.JsonProperty;

public class GetResult implements Serializable {
    private String code;
    private String msg;
    private String result;

    public GetResult() {
    }

    @JsonProperty("code")
    public void setCode(String code) {
        this.code = code;
    }

    @JsonProperty("code")
    public String getCode() {
        return this.code;
    }

    @JsonProperty("msg")
    public void setMsg(String msg) {
        this.msg = msg;
    }

    @JsonProperty("msg")
    public String getMsg() {
        return this.msg;
    }

    @JsonProperty("result")
    public void setResult(String result) {
        this.result = result;
    }

    @JsonProperty("result")
    public String getResult() {
        return this.result;
    }
}
