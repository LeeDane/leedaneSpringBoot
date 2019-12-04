package com.cn.leedane.mall.jingdong.api;

import com.cn.leedane.mall.jingdong.CommUtil;
import com.cn.leedane.model.mall.S_PlatformProductBean;
import com.jd.open.api.sdk.DefaultJdClient;
import com.jd.open.api.sdk.JdClient;
import com.jd.open.api.sdk.JdException;
import com.jd.open.api.sdk.request.mall.WareProductbigfieldGetRequest;
import com.jd.open.api.sdk.response.mall.WareProductbigfieldGetResponse;
import net.sf.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取详情大字段的api
 * @link {http://jos.jd.com/api/detail.htm?apiName=jingdong.ware.productbigfield.get&id=1039}
 * @author LeeDane
 * 2019年11月12日 12:07
 * Version 1.0
 */
public class DetailBigFieldApi {
    /**
     * 获取大字段
    * @param skuid 推广物料（商品链接带ID）
     * @return
     */
    public static String get(String skuid) throws JdException {
        JdClient client = new DefaultJdClient(CommUtil.url, null, CommUtil.appkey, CommUtil.secret);
        WareProductbigfieldGetRequest request=new WareProductbigfieldGetRequest();
        request.setSkuId( skuid);
        request.setField( "wdis" );
        WareProductbigfieldGetResponse response=client.execute(request);
        JSONObject msgObject = JSONObject.fromObject(response.getMsg());
        JSONObject responseObject = msgObject.optJSONObject("jingdong_ware_productbigfield_get_responce");

        if (responseObject.optInt("code") != 0) {
            throw new JdException("京东联盟 "+ request.getApiMethod() +" 网络连接异常");
        }

        return responseObject.optString("wdis");
    }

    /*public static void main(String[] args) throws Exception {
        get("3192581");
    }*/
}