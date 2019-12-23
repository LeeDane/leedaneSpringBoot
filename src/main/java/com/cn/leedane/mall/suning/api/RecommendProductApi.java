package com.cn.leedane.mall.suning.api;

import com.cn.leedane.mall.model.RecommendResult;
import com.cn.leedane.mall.suning.CommUtil;
import com.cn.leedane.model.mall.S_PlatformProductBean;
import com.cn.leedane.utils.EnumUtil;
import com.suning.api.DefaultSuningClient;
import com.suning.api.entity.netalliance.MorerecommendGetRequest;
import com.suning.api.entity.netalliance.MorerecommendGetResponse;
import com.suning.api.exception.SuningApiException;
import com.taobao.api.ApiException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 苏宁推荐商品的api
 * @link {https://open.suning.com/ospos/apipage/toApiMethodDetailMenuNew.do?interCode=suning.netalliance.morerecommend.get}
 * @author LeeDane
 * 2019年11月12日 12:07
 * Version 1.0
 */
public class RecommendProductApi {

    /**
     * 执行推荐商品
     * @param commodityCode 商品编码
     * @param supplierCode 商家编码
     * @param count 暂时没有，只获取10条
     * @return
     * @throws SuningApiException
     */
    public static RecommendResult recommend(String commodityCode, String supplierCode, long count) throws SuningApiException {
        RecommendResult recommendResult = new RecommendResult();
        MorerecommendGetRequest request = new MorerecommendGetRequest();
//        request.setCityCode("025");
        request.setCommodityCode(commodityCode);
//        request.setPicHeight("640");
//        request.setPicLocation("2");
//        request.setPicType("0");
//        request.setPicWidth("640");
        request.setSupplierCode(supplierCode);
//api入参校验逻辑开关，当测试稳定之后建议设置为 false 或者删除该行
        request.setCheckParam(true);
        DefaultSuningClient client = new DefaultSuningClient(CommUtil.serverUrl, CommUtil.appKey, CommUtil.appSecret, "json");

        MorerecommendGetResponse response = client.excute(request);
//        System.out.println("返回json/xml格式数据 :" + response.getBody());
        JSONObject object = JSONObject.fromObject(response.getBody());
        JSONObject responseObject = object.optJSONObject("sn_responseContent");
        JSONObject resultObject = responseObject.optJSONObject("sn_body");
        if(resultObject == null && responseObject.optJSONObject("sn_error") != null){
            throw new SuningApiException(responseObject.optJSONObject("sn_error").optString("error_code"));
        }

        JSONObject morerecommendObject = resultObject.optJSONObject("getMorerecommend");
        JSONArray commodityList = morerecommendObject.optJSONArray("commodityList");

        List<S_PlatformProductBean> items = new ArrayList<S_PlatformProductBean>();
        for (int i = 0; i < commodityList.size(); i++) {
            S_PlatformProductBean suningProductBean = new S_PlatformProductBean();
            JSONObject item = commodityList.optJSONObject(i);
            suningProductBean.setTitle(item.optString("commodityName"));
            suningProductBean.setId(item.optLong("commodityCode"));
            suningProductBean.setAuctionId(item.optLong("commodityCode"));
            suningProductBean.setImg(item.optString("picList"));
            suningProductBean.setOldPrice(item.optDouble("snPrice"));
            suningProductBean.setPrice(item.optDouble("commodityPrice")- item.optDouble("couponPrice"));
            suningProductBean.setPlatform(EnumUtil.ProductPlatformType.苏宁.value);
//            productBean.setCouponShareUrl(item.optString("coupon_share_url"));
            suningProductBean.setShopTitle(item.optString("supplierCode"));
            suningProductBean.setShopId(item.optString("supplierCode"));
            suningProductBean.setClickId("sn_"+ item.optLong("commodityCode") + "-"+ item.optString("supplierCode"));
            items.add(suningProductBean);
        }
        //设置结果集
        recommendResult.setItems(items);
        return recommendResult;
    }
}