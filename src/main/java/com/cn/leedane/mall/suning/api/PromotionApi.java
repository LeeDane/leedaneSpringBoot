package com.cn.leedane.mall.suning.api;

import com.cn.leedane.mall.model.ProductPromotionLinkBean;
import com.cn.leedane.mall.pdd.PddException;
import com.cn.leedane.mall.suning.CommUtil;
import com.suning.api.DefaultSuningClient;
import com.suning.api.entity.netalliance.StorepromotionurlQueryRequest;
import com.suning.api.entity.netalliance.StorepromotionurlQueryResponse;
import com.suning.api.exception.SuningApiException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.net.URLDecoder;

/**
 * 苏宁推广链接api
 * @link {https://open.suning.com/ospos/apipage/toApiMethodDetailMenuNew.do?interCode=suning.netalliance.storepromotionurl.query}
 * @author LeeDane
 * 2019年12月03日 18:47
 * Version 1.0
 */
public class PromotionApi {

    /**
     * 获取苏宁推广链接信息
     * @param adBookId 推广位id
     * @param commCode 商品编码 如果商品编码不为空，则生成商品推广链接，否则生成店铺推广链接
     * @param mertCode 商家编码 10位数字
     * @param urlType url类型 1:长链接 2:短链接
     * @return
     * @throws PddException
     */
    public static ProductPromotionLinkBean getPromotion(String adBookId, String commCode, String mertCode, String urlType) throws SuningApiException {
        ProductPromotionLinkBean promotionLinkBean = new ProductPromotionLinkBean();
        StorepromotionurlQueryRequest request = new StorepromotionurlQueryRequest();
        request.setAdBookId(adBookId);
        request.setCommCode(commCode);
        request.setMertCode(mertCode);
//        request.setSubUser("Axxx");
        request.setUrlType(urlType);
//      api入参校验逻辑开关，当测试稳定之后建议设置为 false 或者删除该行
        request.setCheckParam(true);
        DefaultSuningClient client = new DefaultSuningClient(CommUtil.serverUrl, CommUtil.appKey, CommUtil.appSecret, "json");

        StorepromotionurlQueryResponse response = client.excute(request);
//        System.out.println("返回json/xml格式数据 :" + response.getBody());
        JSONObject object = JSONObject.fromObject(response.getBody());
        JSONObject responseObject = object.optJSONObject("sn_responseContent");
        JSONObject resultObject = responseObject.optJSONObject("sn_body");
        if(resultObject == null && responseObject.optJSONObject("sn_error") != null){
            throw new SuningApiException(responseObject.optJSONObject("sn_error").optString("error_code"));
        }
        JSONObject item = resultObject.optJSONObject("queryStorepromotionurl");

        try {
            promotionLinkBean.setClickUrl(URLDecoder.decode(item.optString("pcExtendUrl"), "utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
            throw new SuningApiException("处理苏宁推广链接异常");
        }
        return promotionLinkBean;
    }
}
