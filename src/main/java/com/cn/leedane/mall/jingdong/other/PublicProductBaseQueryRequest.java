package com.cn.leedane.mall.jingdong.other;

import com.jd.open.api.sdk.internal.util.JsonUtil;
import com.jd.open.api.sdk.request.AbstractRequest;
import com.jd.open.api.sdk.request.JdRequest;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

/*
  *  商品基本信息查询
 * @author LeeDane
 * 2019年11月28日 12:00
 * Version 1.0
 */
public class PublicProductBaseQueryRequest extends AbstractRequest implements JdRequest<WareProductbigfieldGetResponse> {
    private long sku;
    private String areaId = "";
    public PublicProductBaseQueryRequest() {
    }

    public String getApiMethod() {
        return "public.product.base.query";
    }

    public String getAppJsonParams() throws IOException {
        Map<String, Object> pmap = new TreeMap();
        pmap.put("sku", this.sku);
        pmap.put("areaId ", this.areaId);
        return JsonUtil.toJson(pmap);
    }

    public Class<WareProductbigfieldGetResponse> getResponseClass() {
        return WareProductbigfieldGetResponse.class;
    }

    @JsonProperty("sku")
    public long getSku() {
        return sku;
    }

    @JsonProperty("sku")
    public void setSku(long sku) {
        this.sku = sku;
    }

    @JsonProperty("areaId")
    public String getAreaId() {
        return areaId;
    }

    @JsonProperty("areaId")
    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }
}
