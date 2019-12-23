package com.cn.leedane.mall.pdd.api;

import com.cn.leedane.mall.model.ProductPromotionLinkBean;
import com.cn.leedane.mall.pdd.CommUtil;
import com.cn.leedane.mall.pdd.PddException;
import com.cn.leedane.model.mall.S_PlatformProductBean;
import com.cn.leedane.model.mall.S_ShopBean;
import com.cn.leedane.utils.CollectionUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.MoneyUtil;
import com.cn.leedane.utils.StringUtil;
import com.pdd.pop.sdk.common.util.JsonUtil;
import com.pdd.pop.sdk.http.PopClient;
import com.pdd.pop.sdk.http.PopHttpClient;
import com.pdd.pop.sdk.http.api.request.PddDdkGoodsDetailRequest;
import com.pdd.pop.sdk.http.api.response.PddDdkGoodsDetailResponse;
import com.taobao.api.ApiException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 拼多多商品详情的api
 * @link {https://open.pinduoduo.com/#/apidocument/port?portId=pdd.ddk.goods.detail}
 * @author LeeDane
 * 2019年11月12日 12:07
 * Version 1.0
 */
public class DetailSimpleApi {

    /**
     * 通过获取详情的方式获取
     * @param itemId
     * @return
     * @throws ApiException
     */
    public static S_PlatformProductBean getDetail(String itemId) throws PddException {
        PopClient client = new PopHttpClient(CommUtil.clientId, CommUtil.clientSecret);
        PddDdkGoodsDetailRequest request = new PddDdkGoodsDetailRequest();
        List<Long> goodsIdList = new ArrayList<Long>();
        goodsIdList.add(StringUtil.changeObjectToLong(itemId));
        request.setGoodsIdList(goodsIdList);
        /*request.setPid("str");
        request.setCustomParameters("str");
        request.setZsDuoId(0L);
        request.setPlanType(0);
        request.setSearchId("str");*/
        PddDdkGoodsDetailResponse response = null;
        try {
            response = client.syncInvoke(request);
        } catch (Exception e) {
            e.printStackTrace();
            throw new PddException("拼多多api链接异常");
        }
        JSONObject resultObject = JSONObject.fromObject(JsonUtil.transferToJson(response));
        JSONObject responseObject = resultObject.optJSONObject("goods_detail_response");
        JSONObject errResponse = resultObject.optJSONObject("error_response");
        if(errResponse != null && !errResponse.isEmpty()){
            throw new PddException(errResponse.optString("error_msg"));
        }
        JSONArray items = responseObject.optJSONArray("goods_details");
        if(CollectionUtil.isEmpty(items))
            return null;

        //只处理第一条信息，该接口暂时只支持获取一条信息
        return createBean(items.optJSONObject(0));
    }

    /**
     * 根据S_ProductBean对象封装返回的信息
     * @param item
     * @return
     */
    private static S_PlatformProductBean createBean(JSONObject item) throws PddException {
        S_PlatformProductBean pddProductBean = new S_PlatformProductBean();

        S_ShopBean shop = new S_ShopBean();
        pddProductBean.setShopTitle(item.optString("mall_name"));

        StringBuffer imgStr = new StringBuffer();
        JSONArray imgs = item.optJSONArray("goods_gallery_urls");

        if(imgs != null && imgs.size() > 0){
            for(int i = 0; i < imgs.size(); i++){
                imgStr.append(imgs.get(i));
                if(i < imgs.size() -1)
                    imgStr.append(";");
            }
        }
        //优惠券金额
        int couponAmount = item.optInt("coupon_discount") / 100;

        pddProductBean.setImg(imgStr.toString());
        pddProductBean.setPlatform(EnumUtil.ProductPlatformType.拼多多.value);
        pddProductBean.setPrice(item.optDouble("min_group_price") /100);
        pddProductBean.setOldPrice(item.optDouble("min_normal_price") / 100);
        pddProductBean.setCashBack(0d);
        pddProductBean.setCashBackRatio(item.optDouble("promotion_rate") / 10);
        pddProductBean.setCouponAmount(couponAmount);
        pddProductBean.setCouponShareUrl(item.optString("coupon_share_url"));
        pddProductBean.setTitle(item.optString("goods_name"));
        pddProductBean.setId(item.optLong("goods_id"));
        pddProductBean.setAfterCouponPrice(MoneyUtil.twoDecimalPlaces(pddProductBean.getPrice() - couponAmount));
        pddProductBean.setSubtitle(item.optString("opt_name"));
        pddProductBean.setDetail(item.optString("goods_desc"));
        pddProductBean.setClickId("pdd_"+ item.optLong("goods_id"));
        pddProductBean.setSales("已售:"+item.optInt("sales_tip"));
        ProductPromotionLinkBean promotionLinkBean = PromotionApi.getPromotion(item.optLong("goods_id"), "");
        pddProductBean.setShareUrl(promotionLinkBean == null? "" : promotionLinkBean.getClickUrl());
        return pddProductBean;
    }
}