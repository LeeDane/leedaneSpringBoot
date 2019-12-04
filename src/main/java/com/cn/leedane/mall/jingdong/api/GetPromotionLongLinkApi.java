package com.cn.leedane.mall.jingdong.api;

import com.cn.leedane.exception.JdAccessTokenException;
import com.cn.leedane.mall.jingdong.CommUtil;
import com.cn.leedane.model.mall.S_PlatformProductBean;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.StringUtil;
import com.jd.open.api.sdk.DefaultJdClient;
import com.jd.open.api.sdk.JdClient;
import com.jd.open.api.sdk.JdException;
import com.taobao.api.ApiException;
import jd.union.open.goods.promotiongoodsinfo.query.request.UnionOpenGoodsPromotiongoodsinfoQueryRequest;
import jd.union.open.goods.promotiongoodsinfo.query.response.PromotionGoodsResp;
import jd.union.open.promotion.common.get.request.PromotionCodeReq;
import jd.union.open.promotion.common.get.request.UnionOpenPromotionCommonGetRequest;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

/**
 * 获取通用推广商品长连接的api
 * @link {https://union.jd.com/openplatform/api/10421}
 * @author LeeDane
 * 2019年11月12日 12:07
 * Version 1.0
 */
public class GetPromotionLongLinkApi {
    /**
     * 获取长链接，一般出现异常不处理
    * @param materialId 推广物料（商品链接带ID）
     * @return
     */
    public static String get(String materialId) {
        JdClient client = new DefaultJdClient(CommUtil.jinfenUrl, null, CommUtil.appkey, CommUtil.secret);
        UnionOpenPromotionCommonGetRequest request=new UnionOpenPromotionCommonGetRequest();
        PromotionCodeReq promotionCodeReq=new PromotionCodeReq();
        promotionCodeReq.setMaterialId(materialId);
        promotionCodeReq.setSiteId(CommUtil.websiteId);
        request.setPromotionCodeReq(promotionCodeReq);
        Connection.Response res = null;
        S_PlatformProductBean product = new S_PlatformProductBean();
        try {
            String url = CommUtil.buildUrl(request);
            res = Jsoup.connect(url)
                    .ignoreContentType(true)
                    .userAgent("Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0; BIDUBrowser 2.x)")
                    .timeout(8000)
                    .method(Connection.Method.GET)
                    .execute();
            JSONObject object = JSONObject.fromObject(res.body());
            JSONObject responseObject = object.optJSONObject("jd_union_open_promotion_common_get_responce");
            String getResult0 = responseObject.optString("getResult");
            JSONObject getResult = JSONObject.fromObject(getResult0.substring(1, getResult0.length() -1));
            if (getResult.optInt("code") != 200) {
                throw new JdException("京东联盟 jd.union.open.promotion.common.get 网络连接异常");
            }
            return getResult.optJSONObject("data").optString("clickURL");
        }catch (Exception e){
            e.printStackTrace();

        }
        return null;
    }

    /**
     * 根据PromotionGoodsResp对象封装返回的信息
     * @param goodsResp
     * @return
     */
    private static S_PlatformProductBean createBean(PromotionGoodsResp goodsResp){
        if(goodsResp == null)
            return null;
        S_PlatformProductBean product = new S_PlatformProductBean();

        product.setShopTitle(StringUtil.changeNotNull(goodsResp.getShopId()));
        product.setImg(goodsResp.getImgUrl());
        product.setPlatform(EnumUtil.ProductPlatformType.京东.value);
        product.setPrice(goodsResp.getUnitPrice());
        product.setOldPrice(goodsResp.getWlUnitPrice());
        product.setCashBack(0d);
        product.setCashBackRatio(goodsResp.getCommisionRatioPc());
        product.setCouponAmount(0);
        product.setCouponShareUrl(null);
        product.setTitle(goodsResp.getGoodsName());
        product.setId(goodsResp.getSkuId());
        product.setAfterCouponPrice(0d);
        product.setShareUrl(null);
        product.setSubtitle(goodsResp.getCidName() + "-" + goodsResp.getCid2Name() + "-" + goodsResp.getCid3Name());
        return product;
    }
}