package com.cn.leedane.mall.suning.api;

import com.cn.leedane.mall.model.ProductPromotionLinkBean;
import com.cn.leedane.mall.suning.CommUtil;
import com.cn.leedane.model.mall.S_PlatformProductBean;
import com.cn.leedane.model.mall.S_ShopBean;
import com.cn.leedane.utils.CollectionUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.MoneyUtil;
import com.suning.api.DefaultSuningClient;
import com.suning.api.entity.netalliance.CommoditydetailQueryRequest;
import com.suning.api.entity.netalliance.CommoditydetailQueryResponse;
import com.suning.api.entity.union.UnionInfomationGetRequest;
import com.suning.api.entity.union.UnionInfomationGetResponse;
import com.suning.api.exception.SuningApiException;
import com.taobao.api.ApiException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.net.URLDecoder;

/**
 * 苏宁商品详情的api
 * @link {https://open.suning.com/ospos/apipage/toApiMethodDetailMenuNew.do?interCode=suning.netalliance.commoditydetail.query}
 * @author LeeDane
 * 2019年11月12日 12:07
 * Version 1.0
 */
public class DetailSimpleApi {

    /**
     * 通过获取详情的方式获取
     * @param commodityStr 商品编码-商店编码  组合
     * @return
     * @throws SuningApiException
     */
    public static S_PlatformProductBean getDetail(String commodityStr) throws SuningApiException {
        CommoditydetailQueryRequest request = new CommoditydetailQueryRequest();
//        request.setCityCode("025");
        request.setCommodityStr(commodityStr);
//        request.setPicHeight("200");
//        request.setPicWidth("200");
//api入参校验逻辑开关，当测试稳定之后建议设置为 false 或者删除该行
        request.setCheckParam(true);
        DefaultSuningClient client = new DefaultSuningClient(CommUtil.serverUrl, CommUtil.appKey, CommUtil.appSecret, "json");

        CommoditydetailQueryResponse response = client.excute(request);
//        System.out.println("返回json/xml格式数据 :" + response.getBody());
        JSONObject object = JSONObject.fromObject(response.getBody());
        JSONObject responseObject = object.optJSONObject("sn_responseContent");
        JSONObject resultObject = responseObject.optJSONObject("sn_body");
        if(resultObject == null && responseObject.optJSONObject("sn_error") != null){
            throw new SuningApiException(responseObject.optJSONObject("sn_error").optString("error_code"));
        }
        JSONArray items = resultObject.optJSONArray("queryCommoditydetail");

        //只处理第一条信息，该接口暂时只支持获取一条信息
        return createBean(items.optJSONObject(0));
    }

    /**
     * 根据S_ProductBean对象封装返回的信息
     * @param item
     * @return
     */
    private static S_PlatformProductBean createBean(JSONObject item) throws SuningApiException {
        if(item == null)
            return null;
        JSONObject commodityInfo = item.optJSONObject("commodityInfo");
        JSONObject pgInfo = item.optJSONObject("pgInfo");
        JSONObject categoryInfo = item.optJSONObject("categoryInfo");
        JSONObject couponInfo = item.optJSONObject("couponInfo");
        S_PlatformProductBean suningProductBean = new S_PlatformProductBean();

        suningProductBean.setAuctionId(commodityInfo.optLong("commodityCode"));
        suningProductBean.setCashBack(0d);
        suningProductBean.setCashBackRatio(commodityInfo.optDouble("rate"));
        JSONArray pictureUrl = commodityInfo.optJSONArray("pictureUrl");
        if(pictureUrl != null)
            suningProductBean.setImg(pictureUrl.optJSONObject(0).optString("picUrl"));
        suningProductBean.setPlatform(EnumUtil.ProductPlatformType.苏宁.value);
        suningProductBean.setPrice(commodityInfo.optDouble("commodityPrice"));
        suningProductBean.setOldPrice(commodityInfo.optDouble("snPrice"));
        suningProductBean.setTitle(commodityInfo.optString("commodityName"));
        suningProductBean.setSubtitle(commodityInfo.optString("sellingPoint"));
        suningProductBean.setShopTitle(commodityInfo.optString("supplierName"));
        suningProductBean.setShopId(commodityInfo.optString("supplierCode"));
        suningProductBean.setId(commodityInfo.optLong("commodityCode"));

        //获取推广链接
//                suningProductBean.setShareUrl(commodityInfo.optString("couponValue"));
        suningProductBean.setSales("月售:"+commodityInfo.optInt("monthSales"));
        if(couponInfo != null){
            suningProductBean.setCouponAmount(couponInfo.optInt("couponValue"));
            suningProductBean.setCouponShareUrl(couponInfo.optString("couponUrl"));
            suningProductBean.setCouponLeftCount(couponInfo.optInt("couponCount"));
            suningProductBean.setAfterCouponPrice(MoneyUtil.twoDecimalPlaces(suningProductBean.getPrice() - couponInfo.optInt("couponValue")));
        }
        ProductPromotionLinkBean promotionLinkBean = PromotionApi.getPromotion(CommUtil.adBookId, commodityInfo.optString("commodityCode"), commodityInfo.optString("supplierCode"), "1");
        suningProductBean.setShareUrl(promotionLinkBean == null? "" : promotionLinkBean.getClickUrl());
        suningProductBean.setClickId("sn_"+ commodityInfo.optLong("commodityCode")+ "-" + commodityInfo.optString("supplierCode"));
        return suningProductBean;
    }
}