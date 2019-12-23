package com.cn.leedane.mall.jingdong.other;
import com.cn.leedane.mall.jingdong.other.GetResult;
import com.jd.open.api.sdk.response.AbstractResponse;
import org.codehaus.jackson.annotate.JsonProperty;

public class WareProductbigfieldGetResponse extends AbstractResponse {
    private GetResult getResult;

    public WareProductbigfieldGetResponse() {
    }

    @JsonProperty("getResult")
    public void setGetResult(GetResult getResult) {
        this.getResult = getResult;
    }

    @JsonProperty("getResult")
    public GetResult getGetResult() {
        return this.getResult;
    }
}
