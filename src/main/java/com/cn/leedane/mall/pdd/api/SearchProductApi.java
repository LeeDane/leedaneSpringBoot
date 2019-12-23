package com.cn.leedane.mall.pdd.api;

import com.cn.leedane.mall.model.SearchProductRequest;
import com.cn.leedane.mall.model.SearchProductResult;
import com.cn.leedane.mall.pdd.CommUtil;
import com.cn.leedane.mall.pdd.PddException;
import com.cn.leedane.model.mall.S_PlatformProductBean;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.MoneyUtil;
import com.cn.leedane.utils.StringUtil;
import com.pdd.pop.sdk.common.util.JsonUtil;
import com.pdd.pop.sdk.http.PopClient;
import com.pdd.pop.sdk.http.PopHttpClient;
import com.pdd.pop.sdk.http.api.request.PddDdkGoodsSearchRequest;
import com.pdd.pop.sdk.http.api.response.PddDdkGoodsSearchResponse;
import com.taobao.api.ApiException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索商品的api
 * @link {https://open.pinduoduo.com/#/apidocument/port?portId=pdd.ddk.goods.search}
 * @author LeeDane
 * 2019年11月12日 12:07
 * Version 1.0
 */
public class SearchProductApi {

    /**
     * 执行商品查询
     * @param productRequest
     * @return
     * @throws ApiException
     */
    public static SearchProductResult searchProduct(SearchProductRequest productRequest) throws PddException {
        SearchProductResult searchProductResult = new SearchProductResult();
        PopClient client = new PopHttpClient(CommUtil.clientId, CommUtil.clientSecret);

        PddDdkGoodsSearchRequest request = new PddDdkGoodsSearchRequest();
        request.setKeyword(productRequest.getKeyword());
//        request.setOptId(0L);
        request.setPage(StringUtil.changeObjectToInt((productRequest.getPageNo() + 1)));
        request.setPageSize(StringUtil.changeObjectToInt(productRequest.getPageSize()));
        request.setWithCoupon(false);
        request.setSortType(StringUtil.changeObjectToInt(productRequest.getSort()));
        /*request.setWithCoupon(false);
        request.setRangeList("str");
        request.setCatId(0L);
        List<Long> goodsIdList = new ArrayList<Long>();
        goodsIdList.add(0L);
        request.setGoodsIdList(goodsIdList);
        request.setMerchantType(0);
        request.setPid("str");
        request.setCustomParameters("str");
        List<Integer> merchantTypeList = new ArrayList<Integer>();
        merchantTypeList.add(0);
        request.setMerchantTypeList(merchantTypeList);
        request.setIsBrandGoods(false);
        List<Integer> activityTags = new ArrayList<Integer>();
        activityTags.add(0);
        request.setActivityTags(activityTags);*/
        PddDdkGoodsSearchResponse response = null;
        try {
            response = client.syncInvoke(request);
        } catch (Exception e) {
            e.printStackTrace();
            throw new PddException("拼多多api链接异常");
        }
        List<S_PlatformProductBean> pddItems = new ArrayList<S_PlatformProductBean>();
        JSONObject resultObject = JSONObject.fromObject(JsonUtil.transferToJson(response));
        JSONObject responseObject  = resultObject.optJSONObject("goods_search_response");
        JSONObject errResponse = resultObject.optJSONObject("error_response");
        if(errResponse != null && !errResponse.isEmpty()){
            throw new PddException(errResponse.optString("error_msg"));
        }

        //设置总数
        searchProductResult.setTotal(responseObject.optInt("total_count"));

        JSONArray mapDataArray = responseObject.optJSONArray("goods_list");
        for (int i = 0; i < mapDataArray.size(); i++) {
            S_PlatformProductBean pddProductBean = new S_PlatformProductBean();
            JSONObject item = mapDataArray.getJSONObject(i);

            //拼多多所有的价格都是分，处理的时候都要除以100
            //优惠券金额
            int couponAmount = item.optInt("coupon_discount")/100;
            pddProductBean.setAuctionId(item.optLong("goods_id"));
            pddProductBean.setCashBack(0d);
            pddProductBean.setCashBackRatio(item.optDouble("promotion_rate") / 10); //拼多多是千分之一，这里除以10即可
            pddProductBean.setImg(item.optString("goods_image_url")); //去第一张图
            pddProductBean.setPlatform(EnumUtil.ProductPlatformType.拼多多.value);
            pddProductBean.setPrice(item.optDouble("min_group_price")/ 100);
            pddProductBean.setTitle(item.optString("goods_name"));
            pddProductBean.setShopTitle(item.optString("mall_name"));
            pddProductBean.setSubtitle(item.optString("goods_desc"));
            pddProductBean.setCouponAmount(couponAmount);
            pddProductBean.setCouponLeftCount(item.optLong("coupon_remain_quantity"));
            pddProductBean.setShareUrl(item.optString("url"));
            pddProductBean.setCouponShareUrl(item.optString("coupon_share_url"));
            pddProductBean.setId(item.optLong("goods_id"));
            pddProductBean.setClickId("pdd_"+ item.optLong("goods_id"));
            pddProductBean.setAfterCouponPrice(MoneyUtil.twoDecimalPlaces(pddProductBean.getPrice() - couponAmount));
            pddProductBean.setSales("已售:"+item.optInt("sales_tip"));
            pddItems.add(pddProductBean);
        }

        //设置结果集
        searchProductResult.setTaobaoItems(pddItems);
        return searchProductResult;
    }
}