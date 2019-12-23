package com.cn.leedane.mall.pdd.api;

import com.cn.leedane.mall.model.ProductPromotionLinkBean;
import com.cn.leedane.mall.pdd.CommUtil;
import com.cn.leedane.mall.pdd.PddException;
import com.cn.leedane.model.mall.S_PlatformProductBean;
import com.cn.leedane.model.mall.S_PromotionSeatBean;
import com.cn.leedane.model.mall.S_ShopBean;
import com.cn.leedane.utils.CollectionUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.MoneyUtil;
import com.cn.leedane.utils.StringUtil;
import com.pdd.pop.sdk.common.util.JsonUtil;
import com.pdd.pop.sdk.http.PopClient;
import com.pdd.pop.sdk.http.PopHttpClient;
import com.pdd.pop.sdk.http.api.request.PddDdkGoodsDetailRequest;
import com.pdd.pop.sdk.http.api.request.PddDdkGoodsPidGenerateRequest;
import com.pdd.pop.sdk.http.api.response.PddDdkGoodsDetailResponse;
import com.pdd.pop.sdk.http.api.response.PddDdkGoodsPidGenerateResponse;
import com.taobao.api.ApiException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 拼多多创建推广位的api
 * @link {https://open.pinduoduo.com/#/apidocument/port?portId=pdd.ddk.goods.pid.generate}
 * @author LeeDane
 * 2019年11月12日 12:07
 * Version 1.0
 */
public class CreateSeatApi {

    /**
     * 创建
     * @param pIdNameList
     * @return
     * @throws PddException
     */
    public static List<S_PromotionSeatBean> create(List<String> pIdNameList) throws PddException {
        PopClient client = new PopHttpClient(CommUtil.clientId, CommUtil.clientSecret);
        PddDdkGoodsPidGenerateRequest request = new PddDdkGoodsPidGenerateRequest();
        request.setNumber(StringUtil.changeObjectToLong(pIdNameList.size()));
        request.setPIdNameList(pIdNameList);
        PddDdkGoodsPidGenerateResponse response = null;
        System.out.println(JsonUtil.transferToJson(response));
        try {
            response = client.syncInvoke(request);
        } catch (Exception e) {
            e.printStackTrace();
            throw new PddException("拼多多api链接异常");
        }
        JSONObject resultObject = JSONObject.fromObject(JsonUtil.transferToJson(response));
        JSONObject responseObject = resultObject.optJSONObject("p_id_generate_response");
        JSONObject errResponse = resultObject.optJSONObject("error_response");
        if(errResponse != null && !errResponse.isEmpty()){
            throw new PddException(errResponse.optString("error_msg"));
        }

        List<S_PromotionSeatBean> promotionSeatBeans = new ArrayList<>();
        JSONArray  items = responseObject.optJSONArray("p_id_list");
        if(items != null && items.size() > 0){
            for(int i = 0; i < items.size(); i++){
                JSONObject item = items.optJSONObject(i);
                S_PromotionSeatBean promotionSeatBean = new S_PromotionSeatBean();
                promotionSeatBean.setSeatName(item.optString("pid_name"));
                promotionSeatBean.setSeatId(item.optString("p_id"));
                promotionSeatBean.setCreateTime(new Date(item.optLong("create_time")));
                promotionSeatBeans.add(promotionSeatBean);
            }
            return promotionSeatBeans;
        }
        return new ArrayList<>();
    }
}