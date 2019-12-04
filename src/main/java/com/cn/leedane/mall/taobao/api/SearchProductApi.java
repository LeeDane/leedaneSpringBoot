package com.cn.leedane.mall.taobao.api;

import com.cn.leedane.mall.model.SearchProductRequest;
import com.cn.leedane.mall.taobao.CommUtil;
import com.cn.leedane.mall.model.SearchProductResult;
import com.cn.leedane.model.mall.S_PlatformProductBean;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.StringUtil;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.TbkDgMaterialOptionalRequest;
import com.taobao.api.response.TbkDgMaterialOptionalResponse;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索商品的api
 * @link {https://open.taobao.com/api.htm?spm=a2e0r.13193907.0.0.542a24ad8JCoe6&docId=35896&docType=2}
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
    public static SearchProductResult searchProduct(SearchProductRequest productRequest) throws ApiException {
        SearchProductResult searchProductResult = new SearchProductResult();
        TaobaoClient client = new DefaultTaobaoClient(CommUtil.url, CommUtil.appkey, CommUtil.secret);
        TbkDgMaterialOptionalRequest req = new TbkDgMaterialOptionalRequest();
//		req.setStartDsr(10L);
        req.setPageSize(productRequest.getPageSize());
        req.setSort("tk_rate_desc");
        req.setPageNo(productRequest.getPageNo() + 1);
        /*req.setPlatform(1L);
        req.setEndTkRate(1234L);
        req.setStartTkRate(1234L);
        req.setEndPrice(10L);
        req.setStartPrice(10L);
        req.setIsOverseas(false);
        req.setIsTmall(false);
        req.setSort("tk_rate_des");
        req.setItemloc("杭州");
        req.setCat("16,18");*/
        req.setQ(productRequest.getKeyword());
        /*req.setMaterialId(2836L);
        req.setHasCoupon(false);
        req.setIp("13.2.33.4");*/
        req.setAdzoneId(CommUtil.adzoneId);
        /*req.setNeedFreeShipment(true);
        req.setNeedPrepay(true);
        req.setIncludePayRate30(true);
        req.setIncludeGoodRate(true);
        req.setIncludeRfdRate(true);
        req.setNpxLevel(2L);
        req.setEndKaTkRate(1234L);
        req.setStartKaTkRate(1234L);
        req.setDeviceEncrypt("MD5");
        req.setDeviceValue("xxx");
        req.setDeviceType("IMEI");
        req.setLockRateEndTime(1567440000000L);
        req.setLockRateStartTime(1567440000000L);*/
        TbkDgMaterialOptionalResponse rsp = client.execute(req);
        List<S_PlatformProductBean> taobaoItems = new ArrayList<S_PlatformProductBean>();
        String rspBody = rsp.getBody();
        if (StringUtil.isNull(rspBody))
            throw new ApiException("调用淘宝客api，结果返回空串");

        JSONObject resultObject = JSONObject.fromObject(rspBody);

        JSONObject errResponse = resultObject.optJSONObject("error_response");
        if(errResponse != null && !errResponse.isEmpty()){
            throw new ApiException(errResponse.optString("sub_msg"));
        }
        JSONObject responseObject = resultObject.optJSONObject("tbk_dg_material_optional_response");

        //设置总数
        searchProductResult.setTotal(responseObject.optInt("total_results"));

        JSONObject resultListObject = responseObject.optJSONObject("result_list");
        JSONArray mapDataArray = resultListObject.optJSONArray("map_data");
        for (int i = 0; i < mapDataArray.size(); i++) {
            S_PlatformProductBean taobaoProductBean = new S_PlatformProductBean();
            JSONObject object = mapDataArray.getJSONObject(i);

            //优惠券金额
            int couponAmount = object.optInt("coupon_amount");
            taobaoProductBean.setAuctionId(object.optLong("item_id"));
            taobaoProductBean.setCashBack((object.optDouble("commission_rate") / 100 * (object.optDouble("zk_final_price") - couponAmount)));
            taobaoProductBean.setCashBackRatio(object.optDouble("commission_rate") / 100);
            taobaoProductBean.setImg(object.optString("pict_url"));
            taobaoProductBean.setPlatform(EnumUtil.ProductPlatformType.淘宝.value);
            taobaoProductBean.setPrice(object.optDouble("zk_final_price"));
            taobaoProductBean.setTitle(object.optString("title"));
            taobaoProductBean.setShopTitle(object.optString("shop_title"));
            taobaoProductBean.setCouponAmount(couponAmount);
            taobaoProductBean.setCouponLeftCount(object.optLong("coupon_remain_count"));
            taobaoProductBean.setShareUrl(object.optString("url"));
            taobaoProductBean.setCouponShareUrl(object.optString("coupon_share_url"));
            taobaoProductBean.setId(object.optLong("item_id"));
            taobaoItems.add(taobaoProductBean);
        }

        //设置结果集
        searchProductResult.setTaobaoItems(taobaoItems);
        return searchProductResult;
    }
}