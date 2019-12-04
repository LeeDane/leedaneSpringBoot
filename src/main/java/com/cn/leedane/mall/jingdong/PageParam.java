package com.cn.leedane.mall.jingdong;


import java.io.Serializable;
import org.codehaus.jackson.annotate.JsonProperty;

public class PageParam implements Serializable {
    private int pageNum;
    private int pageSize;

    public PageParam() {
    }

    @JsonProperty("pageNum")
    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    @JsonProperty("pageNum")
    public int getPageNum() {
        return this.pageNum;
    }

    @JsonProperty("pageSize")
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    @JsonProperty("pageSize")
    public int getPageSize() {
        return this.pageSize;
    }
}
