package com.cn.leedane.mall.jingdong.api;

import com.cn.leedane.exception.JdAccessTokenException;
import com.cn.leedane.mall.jingdong.CommUtil;
import com.cn.leedane.mall.jingdong.KplOpenXuanpinSearchgoodsRequest;
import com.cn.leedane.mall.jingdong.PageParam;
import com.cn.leedane.mall.jingdong.QueryParam;
import com.cn.leedane.mall.model.SearchProductRequest;
import com.cn.leedane.mall.model.SearchProductResult;
import com.cn.leedane.model.mall.S_PlatformProductBean;
import com.cn.leedane.utils.EnumUtil;
import com.jd.open.api.sdk.JdException;
import com.jd.open.api.sdk.internal.util.CodecUtil;
import com.jd.open.api.sdk.internal.util.HttpUtil;
import com.jd.open.api.sdk.internal.util.StringUtil;
import com.jd.open.api.sdk.request.JdRequest;
import com.taobao.api.ApiException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.util.*;

/**
 * 搜索商品的api
 * 获取推广商品信息接口
 * @link {https://open.jd.com/home/home#/doc/api?apiCateId=582&apiId=3719&apiName=jd.kepler.service.promotion.goodsinfo}
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
    public static SearchProductResult searchProduct(SearchProductRequest productRequest) throws JdException {
        SearchProductResult searchProductResult = new SearchProductResult();
        KplOpenXuanpinSearchgoodsRequest request=new KplOpenXuanpinSearchgoodsRequest();
        QueryParam queryParam=new QueryParam();
        queryParam.setKeywords(productRequest.getKeyword());
        request.setQueryParam(queryParam);
        PageParam pageParam=new PageParam();
        pageParam.setPageSize((int)productRequest.getPageSize());
        request.setPageParam(pageParam);
        request.setOrderField(0);
        Connection.Response res = null;
        List<S_PlatformProductBean> jingdongItems = new ArrayList<S_PlatformProductBean>();
        try {
            String url = CommUtil.buildUrl(request);
            res = Jsoup.connect(url)
                    .ignoreContentType(true)
                    .userAgent("Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0; BIDUBrowser 2.x)")
                    .timeout(8000)
                    .method(Connection.Method.GET)
                    .execute();
            JSONObject object = JSONObject.fromObject(res.body());
            JSONObject responseObject = object.optJSONObject("jd_kpl_open_xuanpin_searchgoods_responce");
            JSONObject searchgoodsResult = responseObject.optJSONObject("searchgoodsResult");
            JSONObject resultObject = searchgoodsResult.optJSONObject("result");
            JSONArray queryVos = resultObject.optJSONArray("queryVo");
            for (int i = 0; i < queryVos.size(); i++) {
                S_PlatformProductBean jingdongProductBean = new S_PlatformProductBean();
                JSONObject queryVo = queryVos.optJSONObject(i);
                jingdongProductBean.setAuctionId(queryVo.optLong("skuId"));
                jingdongProductBean.setCashBack(0d);
                jingdongProductBean.setCashBackRatio(queryVo.optDouble("commisionRatioWl"));
                jingdongProductBean.setImg(CommUtil.imgBasePath + queryVo.optString("imageUrl"));
                jingdongProductBean.setPlatform(EnumUtil.ProductPlatformType.京东.value);
                jingdongProductBean.setPrice(queryVo.optDouble("price"));
                jingdongProductBean.setTitle(queryVo.optString("wareName"));
                jingdongProductBean.setShopTitle(queryVo.optString("brand_id"));
                jingdongProductBean.setId(queryVo.optLong("skuId"));
                jingdongItems.add(jingdongProductBean);
            }
//            throw new JdAccessTokenException(object.optString("msg"));
        } catch (Exception e) {
            e.printStackTrace();
            throw new JdAccessTokenException("京东授权网络连接异常");
        }
        //设置结果集
        searchProductResult.setTaobaoItems(jingdongItems);
        return searchProductResult;

    }
}