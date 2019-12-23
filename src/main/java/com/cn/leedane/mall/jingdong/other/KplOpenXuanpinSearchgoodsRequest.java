package com.cn.leedane.mall.jingdong.other;

import com.jd.open.api.sdk.internal.util.JsonUtil;
import com.jd.open.api.sdk.request.AbstractRequest;
import com.jd.open.api.sdk.request.JdRequest;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import org.codehaus.jackson.annotate.JsonProperty;

public class KplOpenXuanpinSearchgoodsRequest extends AbstractRequest implements JdRequest<KplOpenXuanpinSearchgoodsResponse> {
    private QueryParam queryParam;
    private PageParam pageParam;
    private Integer orderField;

    public KplOpenXuanpinSearchgoodsRequest() {
    }

    public String getApiMethod() {
        return "jd.kpl.open.xuanpin.searchgoods";
    }

    public String getAppJsonParams() throws IOException {
        Map<String, Object> pmap = new TreeMap();
        pmap.put("queryParam", this.queryParam);
        pmap.put("pageParam", this.pageParam);
        pmap.put("orderField", this.orderField);
        return JsonUtil.toJson(pmap);
    }

    public Class<KplOpenXuanpinSearchgoodsResponse> getResponseClass() {
        return KplOpenXuanpinSearchgoodsResponse.class;
    }

    @JsonProperty("queryParam")
    public void setQueryParam(QueryParam queryParam) {
        this.queryParam = queryParam;
    }

    @JsonProperty("queryParam")
    public QueryParam getQueryParam() {
        return this.queryParam;
    }

    @JsonProperty("pageParam")
    public void setPageParam(PageParam pageParam) {
        this.pageParam = pageParam;
    }

    @JsonProperty("pageParam")
    public PageParam getPageParam() {
        return this.pageParam;
    }

    @JsonProperty("orderField")
    public void setOrderField(Integer orderField) {
        this.orderField = orderField;
    }

    @JsonProperty("orderField")
    public Integer getOrderField() {
        return this.orderField;
    }
}
