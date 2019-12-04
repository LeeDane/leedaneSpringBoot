package com.cn.leedane.mall.jingdong;
import java.io.Serializable;
import org.codehaus.jackson.annotate.JsonProperty;

public class QueryVo implements Serializable {
    private String keywords;
    private String skuId;
    private String wareName;
    private String imageUrl;
    private String price;
    private String venderId;
    private String brandId;
    private String qtty30;
    private String opTime;
    private String commisionRatioWl;

    public QueryVo() {
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

    @JsonProperty("wareName")
    public void setWareName(String wareName) {
        this.wareName = wareName;
    }

    @JsonProperty("wareName")
    public String getWareName() {
        return this.wareName;
    }

    @JsonProperty("imageUrl")
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @JsonProperty("imageUrl")
    public String getImageUrl() {
        return this.imageUrl;
    }

    @JsonProperty("price")
    public void setPrice(String price) {
        this.price = price;
    }

    @JsonProperty("price")
    public String getPrice() {
        return this.price;
    }

    @JsonProperty("vender_id")
    public void setVenderId(String venderId) {
        this.venderId = venderId;
    }

    @JsonProperty("vender_id")
    public String getVenderId() {
        return this.venderId;
    }

    @JsonProperty("brand_id")
    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }

    @JsonProperty("brand_id")
    public String getBrandId() {
        return this.brandId;
    }

    @JsonProperty("qtty_30")
    public void setQtty30(String qtty30) {
        this.qtty30 = qtty30;
    }

    @JsonProperty("qtty_30")
    public String getQtty30() {
        return this.qtty30;
    }

    @JsonProperty("op_time")
    public void setOpTime(String opTime) {
        this.opTime = opTime;
    }

    @JsonProperty("op_time")
    public String getOpTime() {
        return this.opTime;
    }

    @JsonProperty("commisionRatioWl")
    public void setCommisionRatioWl(String commisionRatioWl) {
        this.commisionRatioWl = commisionRatioWl;
    }

    @JsonProperty("commisionRatioWl")
    public String getCommisionRatioWl() {
        return this.commisionRatioWl;
    }
}
