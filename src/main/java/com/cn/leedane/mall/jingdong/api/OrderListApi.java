package com.cn.leedane.mall.jingdong.api;

import com.cn.leedane.mall.jingdong.CommUtil;
import com.cn.leedane.model.mall.S_OrderDetailBean;
import com.cn.leedane.model.mall.S_PlatformProductBean;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.OptionUtil;
import com.cn.leedane.utils.StringUtil;
import com.jd.open.api.sdk.DefaultJdClient;
import com.jd.open.api.sdk.JdClient;
import com.jd.open.api.sdk.JdException;
import com.jd.open.api.sdk.request.mall.WareProductbigfieldGetRequest;
import com.jd.open.api.sdk.response.mall.WareProductbigfieldGetResponse;
import jd.union.open.order.query.request.OrderReq;
import jd.union.open.order.query.request.UnionOpenOrderQueryRequest;
import jd.union.open.order.query.response.UnionOpenOrderQueryResponse;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 获取订单列表的api
 * @link {https://union.jd.com/openplatform/api/10419}
 * @author LeeDane
 * 2019年11月12日 12:07
 * Version 1.0
 */
public class OrderListApi {
    /**
     * 获取订单列表
     * @param pageNo 页码，返回第几页结果
     * @param pageSize 每页包含条数，上限为500
     * @param type 订单时间查询类型(1：下单时间，2：完成时间，3：更新时间)
     * @param time 查询时间，建议使用分钟级查询，格式：yyyyMMddHH、yyyyMMddHHmm或yyyyMMddHHmmss，如201811031212 的查询范围从12:12:00--12:12:59
     * @return
     * @throws JdException
     */
    public static List<S_OrderDetailBean> get(int pageNo, int pageSize, int type, String time) throws JdException {
        UnionOpenOrderQueryRequest request=new UnionOpenOrderQueryRequest();
        OrderReq orderReq = new OrderReq();
        orderReq.setPageNo(pageNo);
        orderReq.setPageSize(pageSize);
        orderReq.setType(type);
        orderReq.setTime(time);
        request.setOrderReq(orderReq);
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
            JSONObject responseObject = object.optJSONObject("jd_union_open_order_query_responce");
            String queryResult0 = responseObject.optString("queryResult");
            JSONObject queryResultObject = JSONObject.fromObject(queryResult0.substring(1, queryResult0.length() -1));
            if(queryResultObject.optInt("code") != 200){
                throw new JdException("京东联盟 "+ request.getApiMethod() +" 网络连接异常");
            }
            JSONArray data = queryResultObject.optJSONArray("data");
            if(data != null){
                List<S_OrderDetailBean> detailBeans = new ArrayList<>();
                for(int i = 0; i < data.size(); i++){
                    JSONObject dataItem = data.getJSONObject(i);
                    //订单包含的商品数量
                    JSONArray skuList = dataItem.optJSONArray("skuList");
                    for(int j = 0; j < skuList.size(); j++){
                        JSONObject item = skuList.optJSONObject(j);
                        //每个商品是一条记录进行保存
                        S_OrderDetailBean detailBean = new S_OrderDetailBean();
                        detailBean.setOrderBuyNumer(item.optInt("skuNum") - item.optInt("skuReturnNum"));//商品数量 - 商品已退货数量
                        detailBean.setOrderCashBack(item.optDouble("estimateCosPrice")); //预估计佣金额，即用户下单的金额(已扣除优惠券、白条、支付优惠、进口税，未扣除红包和京豆)，有时会误扣除运费券金额，完成结算时会在实际计佣金额中更正。如订单完成前发生退款，此金额也会更新。
                        detailBean.setOrderCashBackRatio(item.optDouble("commissionRate")); //佣金比例
                        detailBean.setOrderCategory(item.optString("skuName"));
                        detailBean.setOrderCode(dataItem.optLong("orderId"));//订单ID
                        detailBean.setOrderSubCode(dataItem.optLong("orderId")); //订单ID
                        detailBean.setOrderCreateTime(new Date(dataItem.optLong("orderTime"))); //下单时间(时间戳，毫秒)
                        detailBean.setOrderPayMoney(item.optDouble("estimateCosPrice"));
                        detailBean.setOrderPayTime(new Date(dataItem.optLong("orderTime")));
                        detailBean.setOrderProductId(item.optLong("skuId")); //商品ID
                        detailBean.setOrderSettlementMoney(item.optDouble("actualCosPrice")); //实际计算佣金的金额。订单完成后，会将误扣除的运费券金额更正。如订单完成后发生退款，此金额会更新。
                        detailBean.setOrderSettlementTime(new Date(dataItem.optLong("finishTime"))); //订单完成时间(时间戳，毫秒)
                        detailBean.setOrderType(item.optInt("orderEmt") == 1 ? "PC": "无线"); //下单设备(1:PC,2:无线)
                        detailBean.setPlatform(EnumUtil.ProductPlatformType.京东.value);
                        detailBean.setCreateTime(new Date());
                        detailBean.setCreateUserId(OptionUtil.adminUser.getId());
                        detailBean.setOrderSettlementCashBack(item.optDouble("actualFee"));//推客获得的实际佣金（实际计佣金额*佣金比例*最终比例）。如订单完成后发生退款，此金额会更新。
                        detailBean.setStatus(getSystemStatus(item.optInt("validCode")));
                        detailBean.setProductTitle(item.optString("skuName"));
                        detailBeans.add(detailBean);
                    }
                }
                return detailBeans;
            }
            return new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            throw new JdException("京东联盟网络连接异常");
        }
    }

    /**
     * 将平台的状态转化成系统相对应的平台状态码
     * @param status 订单维度的有效码，不建议使用，可以用订单行sku维度validCode参考,
     *               （-1：未知,2.无效-拆单,3.无效-取消,4.无效-京东帮帮主订单,5.无效-账号异常,6.无效-赠品类目不返佣,
     *               7.无效-校园订单,8.无效-企业订单,9.无效-团购订单,10.无效-开增值税专用发票订单,
     *               11.无效-乡村推广员下单,12.无效-自己推广自己下单,13.无效-违规订单,14.无效-来源与备案网址不符,
     *               15.待付款,16.已付款,17.已完成,18.已结算（5.9号不再支持结算状态回写展示））
     * @return
     */
    private static int getSystemStatus(int status) {
        if(status == 16)
            return EnumUtil.MallOrderType.待结算佣金.value;
        if(status == 18)
            return EnumUtil.MallOrderType.待支付佣金.value;
        if(status == 17)//已经完成
            return EnumUtil.MallOrderType.已完成.value;
        return status;
    }

    /*public static void main(String[] args) throws Exception {
        get(0, 20, 1, "2019110711");
    }*/
}