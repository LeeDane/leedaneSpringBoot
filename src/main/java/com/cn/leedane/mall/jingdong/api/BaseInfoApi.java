package com.cn.leedane.mall.jingdong.api;

import com.cn.leedane.mall.jingdong.CommUtil;
import com.cn.leedane.mall.jingdong.PublicProductBaseQueryRequest;
import com.cn.leedane.model.mall.S_PlatformProductBean;
import com.cn.leedane.utils.EnumUtil;
import com.jd.open.api.sdk.DefaultJdClient;
import com.jd.open.api.sdk.JdClient;
import com.jd.open.api.sdk.JdException;
import com.jd.open.api.sdk.domain.mall.ProductWrapService.ProductBase;
import com.jd.open.api.sdk.request.mall.WareProductbigfieldGetRequest;
import com.jd.open.api.sdk.response.mall.WareProductbigfieldGetResponse;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.util.HashMap;
import java.util.Map;

/**
 * 获取商品基本信息的api
 * 可以获取店铺信息、商品原价信息，照片集合信息
 * @link {http://jos.jd.com/api/detail.htm?id=3701}
 * @author LeeDane
 * 2019年11月12日 12:07
 * Version 1.0
 */
public class BaseInfoApi {
    /**
     * 获取大字段
    * @param skuid 推广物料（商品链接带ID）
     * @return
     */
    public static JSONObject get(long skuid) throws JdException {
        PublicProductBaseQueryRequest request=new PublicProductBaseQueryRequest();
        request.setSku(skuid);
        Connection.Response res = null;
        try {
            String url = CommUtil.buildUrl(request);
            res = Jsoup.connect(url)
                    .ignoreContentType(true)
                    .userAgent("Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0; BIDUBrowser 2.x)")
                    .timeout(8000)
                    .method(Connection.Method.GET)
                    .execute();
            JSONObject object = JSONObject.fromObject(res.body());
            JSONObject responseObject = object.optJSONObject("public_product_base_query_responce");
            if(responseObject.optInt("code") != 0){
                throw new JdException("京东联盟 "+ request.getApiMethod() +" 网络连接异常");
            }

            JSONObject queryResult = responseObject.optJSONObject("queryResult");
            if(queryResult.optInt("resultCode") != 0){
                throw new JdException("京东联盟 "+ request.getApiMethod() +" 网络连接异常");
            }
            return queryResult.optJSONObject("result");
        } catch (Exception e) {
            e.printStackTrace();
            throw new JdException("京东联盟 "+ request.getApiMethod() +" 网络连接异常");
        }

    }
}