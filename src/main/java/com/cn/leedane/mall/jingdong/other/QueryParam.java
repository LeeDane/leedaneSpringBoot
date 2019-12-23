package com.cn.leedane.mall.jingdong.other;


import java.io.Serializable;
import org.codehaus.jackson.annotate.JsonProperty;

public class QueryParam implements Serializable {
    private String keywords;
    private String skuId;

    public QueryParam() {
    }

    @JsonProperty("keywords")
    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    @JsonProperty("keywords")
    public String getKeywords() {
        return this.keywords;
    }

    @JsonProperty("skuId")
    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    @JsonProperty("skuId")
    public String getSkuId() {
        return this.skuId;
    }
}
