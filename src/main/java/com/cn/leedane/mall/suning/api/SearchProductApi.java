package com.cn.leedane.mall.suning.api;

import com.cn.leedane.exception.JdAccessTokenException;
import com.cn.leedane.mall.model.SearchProductRequest;
import com.cn.leedane.mall.model.SearchProductResult;
import com.cn.leedane.mall.suning.CommUtil;
import com.cn.leedane.model.mall.S_PlatformProductBean;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.StringUtil;
import com.jd.open.api.sdk.JdException;
import com.suning.api.DefaultSuningClient;
import com.suning.api.entity.netalliance.SearchcommodityQueryRequest;
import com.suning.api.entity.netalliance.SearchcommodityQueryResponse;
import com.suning.api.exception.SuningApiException;
import com.taobao.api.ApiException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * 搜索商品的api
 * @link {https://open.suning.com/ospos/apipage/toApiMethodDetailMenuNew.do?interCode=suning.netalliance.searchcommodity.query}
 * @author LeeDane
 * 2019年11月12日 12:07
 * Version 1.0
 */
public class SearchProductApi {

    /**
     * 执行商品查询
     * 开普勒商品API
     * 商品搜索接口（支持佣金排序）
     * http://open.jd.com/home/home#/doc/api?apiCateId=572&apiId=3729&apiName=jd.kpl.open.xuanpin.searchgoods
     * @param productRequest 商品编号,支持同时查询100个sku，多个用,分割开
     * @return
     * @throws ApiException
     */
    public static SearchProductResult searchProduct(SearchProductRequest productRequest) throws SuningApiException {
        SearchProductResult searchProductResult = new SearchProductResult();
        SearchcommodityQueryRequest request = new SearchcommodityQueryRequest();
       /* request.setBranch("1");
        request.setCityCode("025");
        request.setEndPrice("20.00");*/
        request.setKeyword(productRequest.getKeyword());
        request.setPageIndex((productRequest.getPageNo() + 1) + "");
       /* request.setPgSearch("1");
        request.setPicHeight("200");
        request.setPicWidth("200");
        request.setSaleCategoryCode("50000");*/
        request.setSize(productRequest.getPageSize() +"");
        request.setSortType(productRequest.getSort());
       /* request.setStartPrice("10.00");
        request.setSuningService("1");*/
        //api入参校验逻辑开关，当测试稳定之后建议设置为 false 或者删除该行
        request.setCheckParam(true);
        DefaultSuningClient client = new DefaultSuningClient(CommUtil.serverUrl, CommUtil.appKey, CommUtil.appSecret, "json");
        SearchcommodityQueryResponse response = client.excute(request);
//        System.out.println("返回json/xml格式数据 :" + response.getBody());

        try {

            JSONObject object = JSONObject.fromObject(response.getBody());
            System.out.println(object.toString());
            JSONObject responseObject = object.optJSONObject("sn_responseContent");
            JSONObject resultObject = responseObject.optJSONObject("sn_body");
            if(resultObject == null && responseObject.optJSONObject("sn_error") != null){
                throw new SuningApiException(responseObject.optJSONObject("sn_error").optString("error_code"));
            }

            JSONArray queryVos = resultObject.optJSONArray("querySearchcommodity");
            List<S_PlatformProductBean> suningBeans = new ArrayList<>();
            for (int i = 0; i < queryVos.size(); i++) {
                JSONObject queryVo = queryVos.optJSONObject(i);
                JSONObject commodityInfo = queryVo.optJSONObject("commodityInfo");
                JSONObject pgInfo = queryVo.optJSONObject("pgInfo");
                JSONObject categoryInfo = queryVo.optJSONObject("categoryInfo");
                JSONObject couponInfo = queryVo.optJSONObject("couponInfo");

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
                suningProductBean.setSales("月售:"+ commodityInfo.optInt("monthSales"));
                if(couponInfo != null){
                    suningProductBean.setCouponAmount(couponInfo.optInt("couponValue"));
                    suningProductBean.setCouponShareUrl(couponInfo.optString("couponUrl"));
                    suningProductBean.setCouponLeftCount(couponInfo.optInt("couponCount"));
                }
                suningProductBean.setClickId("sn_"+ commodityInfo.optLong("commodityCode")+ "-"+ commodityInfo.optString("supplierCode"));
                suningBeans.add(suningProductBean);
            }
            //设置结果集
            searchProductResult.setTaobaoItems(suningBeans);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SuningApiException("苏宁网络连接异常");
        }
        return searchProductResult;
    }

    public static void main(String[] args) throws SuningApiException {
        /*SearchProductRequest request = new SearchProductRequest();
        request.setSort("1");
        request.setKeyword("小米手机");
        request.setPageNo(0L);
        request.setPageSize(12L);
        searchProduct(request);*/
       /* String url = "https%3A%2F%2Fsugs.suning.com%2Foutstation.htm%3Fp%3DVANWQ1YIVw1AEx9icnlmXUNPVksBRH5gMStZWXdnfXJbSVcNYjlkbhoWGBYZGhkaKRQXbxdCTV12UVNGGloLHFRVUEIKCQdEWBFaajY0YlMOSFdKXEJ-JWQzXRNzLywyFQMVS31qJCxFQl1FXQRaXztMSDkIFxYFb1ZRQ1ZcCx1RUlRcAggHCRIXE2x9emZKGg4MHxgKM3p2bEpdcWJ9a1BBS0BkZWVqEBMQHBgeCgk";
        try {
            System.out.println(URLDecoder.decode(url, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }*/
    }
}