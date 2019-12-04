package com.cn.leedane.mall.taobao.api;

import com.cn.leedane.handler.ZXingCodeHandler;
import com.cn.leedane.mall.model.ProductPromotionLinkBean;
import com.cn.leedane.utils.StringUtil;
import com.google.zxing.WriterException;
import com.taobao.api.ApiException;

/**
 * 淘宝链接api
 * @link {https://open.pinduoduo.com/#/apidocument/port?portId=pdd.ddk.goods.promotion.url.generate}
 * @author LeeDane
 * 2019年12月03日 18:47
 * Version 1.0
 */
public class PromotionApi {

    /**
     *
     * 获取淘宝推广链接信息
     * @param title 标题，不能为空，不然无法转发淘口令
     * @param img 主图地址，可以为空
     * @param longLink
     * @param longCouponLink 获取优惠券的地址信息，可以为空
     * @return
     * @throws ApiException
     */
    public static ProductPromotionLinkBean getPromotion(String title, String img, String longLink, String longCouponLink) throws ApiException, WriterException {
        boolean hasCoupon = StringUtil.isNotNull(longCouponLink);

        //封装查询请求参数
        String[] links = new String[hasCoupon ? 2: 1];
        links[0] = longLink;
        if(hasCoupon)
            links[1] = longCouponLink;

        SpreadApi.ShortLinkResult shortLinkResult = SpreadApi.toShort(links);

        ProductPromotionLinkBean promotionLinkBean = new ProductPromotionLinkBean();
        promotionLinkBean.setClickUrl(longLink);
        promotionLinkBean.setCouponLink(longCouponLink);

        if(shortLinkResult.getTotal() > 0){
            if("OK".equalsIgnoreCase(shortLinkResult.getResults().get(0).getErrCode()))
                promotionLinkBean.setShortLinkUrl(shortLinkResult.getResults().get(0).getContent());
            if(hasCoupon){
                if("OK".equalsIgnoreCase(shortLinkResult.getResults().get(1).getErrCode()))
                    promotionLinkBean.setCouponShortLinkUrl(shortLinkResult.getResults().get(1).getContent());
            }

        }else{
            throw new ApiException("无法生成共享链接！");
        }

        //获取淘口令
        promotionLinkBean.setTaoToken(TpwdApi.toTpwd(promotionLinkBean.getShortLinkUrl(), img, title));
        if(hasCoupon)
            promotionLinkBean.setCouponLinkTaoToken(TpwdApi.toTpwd(promotionLinkBean.getCouponShortLinkUrl(), img, title));

        promotionLinkBean.setQrCodeUrl(ZXingCodeHandler.createQRCode(promotionLinkBean.getShortLinkUrl(), 100));
        if(hasCoupon)
            promotionLinkBean.setQrCouponCodeUrl(ZXingCodeHandler.createQRCode(promotionLinkBean.getCouponShortLinkUrl(), 100));

        return promotionLinkBean;
    }
}
