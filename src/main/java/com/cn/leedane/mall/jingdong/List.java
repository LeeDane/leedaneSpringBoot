package com.cn.leedane.mall.jingdong;
import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonProperty;

public class List implements Serializable {
    private QueryVo vo;

    public List() {
    }

    @JsonProperty("vo")
    public void setVo(QueryVo vo) {
        this.vo = vo;
    }

    @JsonProperty("vo")
    public QueryVo getVo() {
        return this.vo;
    }
}
