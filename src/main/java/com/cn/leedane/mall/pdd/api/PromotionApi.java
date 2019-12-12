package com.cn.leedane.mall.pdd.api;

import com.cn.leedane.handler.ZXingCodeHandler;
import com.cn.leedane.mall.model.ProductPromotionLinkBean;
import com.cn.leedane.mall.pdd.CommUtil;
import com.cn.leedane.mall.pdd.PddException;
import com.cn.leedane.mall.taobao.api.SpreadApi;
import com.cn.leedane.mall.taobao.api.TpwdApi;
import com.cn.leedane.utils.StringUtil;
import com.google.zxing.WriterException;
import com.pdd.pop.sdk.common.util.JsonUtil;
import com.pdd.pop.sdk.http.PopClient;
import com.pdd.pop.sdk.http.PopHttpClient;
import com.pdd.pop.sdk.http.api.request.PddDdkGoodsPromotionUrlGenerateRequest;
import com.pdd.pop.sdk.http.api.response.PddDdkGoodsPromotionUrlGenerateResponse;
import com.taobao.api.ApiException;

import java.util.ArrayList;
import java.util.List;

/**
 * 拼多多推广链接api
 * @link {https://open.pinduoduo.com/#/apidocument/port?portId=pdd.ddk.goods.promotion.url.generate}
 * @author LeeDane
 * 2019年12月03日 18:47
 * Version 1.0
 */
public class PromotionApi {

    /**
     *
     * 获取拼多多推广链接信息
     * @param goodId
     * @return
     * @throws ApiException
     */
    public static ProductPromotionLinkBean getPromotion(long goodId, String title) throws PddException {
        ProductPromotionLinkBean promotionLinkBean = new ProductPromotionLinkBean();
        PopClient client = new PopHttpClient(CommUtil.clientId, CommUtil.clientSecret);
        PddDdkGoodsPromotionUrlGenerateRequest request = new PddDdkGoodsPromotionUrlGenerateRequest();
        request.setPId(CommUtil.pid);
        List<Long> goodsIdList = new ArrayList<Long>();
        goodsIdList.add(goodId);
        request.setGoodsIdList(goodsIdList);
        request.setGenerateShortUrl(true);
        request.setMultiGroup(true);
        request.setCustomParameters(title);
        request.setGenerateWeappWebview(false);
//        request.setZsDuoId(0L);
        request.setGenerateWeApp(true); //是否生成小程序推广
//        request.setGenerateWeiboappWebview(false); //是否生成微博推广链接
        request.setGenerateMallCollectCoupon(true); //是否生成店铺收藏券推广链接
        request.setGenerateSchemaUrl(false); //是否返回 schema URL
        request.setGenerateQqApp(false); //是否生成qq小程序
        PddDdkGoodsPromotionUrlGenerateResponse response = null;
        try {
            response = client.syncInvoke(request);
            PddDdkGoodsPromotionUrlGenerateResponse.GoodsPromotionUrlGenerateResponseGoodsPromotionUrlListItem item = response.getGoodsPromotionUrlGenerateResponse().getGoodsPromotionUrlList().get(0);
            promotionLinkBean.setClickUrl(item.getUrl());
            promotionLinkBean.setShortLinkUrl(item.getShortUrl());
            promotionLinkBean.setCouponLink(item.getMobileUrl());
            promotionLinkBean.setCouponShortLinkUrl(item.getMobileShortUrl());
//            promotionLinkBean.setCouponLink(item.getShortUrl());
        } catch (Exception e) {
            e.printStackTrace();
            throw new PddException("处理拼多多推广链接异常");
        }
        return promotionLinkBean;
    }
}
