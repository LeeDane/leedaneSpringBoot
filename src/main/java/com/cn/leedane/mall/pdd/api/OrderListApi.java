package com.cn.leedane.mall.pdd.api;

import com.cn.leedane.mall.pdd.PddException;
import com.cn.leedane.model.mall.S_OrderDetailBean;
import com.cn.leedane.utils.CollectionUtil;
import com.cn.leedane.utils.DateUtil;
import com.cn.leedane.utils.StringUtil;
import com.pdd.pop.sdk.common.util.JsonUtil;
import com.pdd.pop.sdk.http.PopClient;
import com.pdd.pop.sdk.http.PopHttpClient;
import com.pdd.pop.sdk.http.api.request.PddDdkOrderListRangeGetRequest;
import com.pdd.pop.sdk.http.api.response.PddDdkOrderListRangeGetResponse;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.Date;
import java.util.List;

/**
 * 获取订单列表的api
 * @link {https://open.pinduoduo.com/#/apidocument/port?portId=pdd.ddk.order.list.range.get}
 * @author LeeDane
 * 2019年11月12日 12:07
 * Version 1.0
 */
public class OrderListApi {
    /**
     * 获取订单列表
     * @param startTime 支付起始时间，如：2019-05-05 00:00:00
     * @param endTime 支付结束时间，如：2019-05-05 00:00:00
     * @param lastOrderId 上一次的迭代器id(第一次不填)
     * @param pageSize 每次请求多少条，建议300
     * @return
     * @throws PddException
     */
    public static List<S_OrderDetailBean> get(Date startTime, Date endTime, String lastOrderId, int pageSize) throws PddException {
        PopClient client = new PopHttpClient(com.cn.leedane.mall.pdd.CommUtil.clientId, com.cn.leedane.mall.pdd.CommUtil.clientSecret);

        //结束时间不能大于当前系统时间
        if(endTime.getTime() > System.currentTimeMillis())
            endTime = new Date();
        PddDdkOrderListRangeGetRequest request = new PddDdkOrderListRangeGetRequest();
        request.setStartTime(DateUtil.DateToString(startTime));
        if(StringUtil.isNotNull(lastOrderId))
            request.setLastOrderId(lastOrderId);
        request.setPageSize(pageSize < 1 ? 250: pageSize);
        request.setEndTime(DateUtil.DateToString(endTime));
        PddDdkOrderListRangeGetResponse response = null;
        try {
            response = client.syncInvoke(request);
        } catch (Exception e) {
            e.printStackTrace();
            throw new PddException("拼多多api链接异常");
        }
        System.out.println(JsonUtil.transferToJson(response));
        JSONObject resultObject = JSONObject.fromObject(JsonUtil.transferToJson(response));
        JSONObject responseObject = resultObject.optJSONObject("goods_detail_response");
        JSONObject errResponse = resultObject.optJSONObject("error_response");
        if(errResponse != null && !errResponse.isEmpty()){
            throw new PddException(errResponse.optString("error_msg"));
        }
        JSONArray items = responseObject.optJSONArray("goods_details");
        if(CollectionUtil.isEmpty(items))
            return null;

        return null;
    }
}