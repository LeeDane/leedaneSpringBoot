package com.cn.leedane.mall.jingdong;

import com.jd.open.api.sdk.internal.util.JsonUtil;
import com.jd.open.api.sdk.request.AbstractRequest;
import com.jd.open.api.sdk.request.JdRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.codehaus.jackson.annotate.JsonProperty;

public class WareProductbigfieldGetRequest extends AbstractRequest implements JdRequest<WareProductbigfieldGetResponse> {
    private String wid;
    private String sku_id;
    private List<String> fields;

    public WareProductbigfieldGetRequest() {
    }

    public String getApiMethod() {
        return "jingdong.ware.productbigfield.get";
    }

    public String getAppJsonParams() throws IOException {
        Map<String, Object> pmap = new TreeMap();
        pmap.put("wid", this.wid);
        pmap.put("sku_id", this.wid);
        pmap.put("fields", this.fields);
        return JsonUtil.toJson(pmap);
    }

    public Class<WareProductbigfieldGetResponse> getResponseClass() {
        return WareProductbigfieldGetResponse.class;
    }

    @JsonProperty("wid")
    public void setWid(String wid) {
        this.wid = wid;
    }

    @JsonProperty("wid")
    public String getWid() {
        return this.wid;
    }

    @JsonProperty("fields")
    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    @JsonProperty("fields")
    public List<String> getFields() {
        return this.fields;
    }

    @JsonProperty("sku_id")
    public String getSku_id() {
        return sku_id;
    }

    @JsonProperty("sku_id")
    public void setSku_id(String sku_id) {
        this.sku_id = sku_id;
    }
}
