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
import jd.union.open.goods.promotiongoodsinfo.query.response.UnionOpenGoodsPromotiongoodsinfoQueryResponse;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 商品详情的api
 * @link {https://union.jd.com/openplatform/api/10422}
 * @author LeeDane
 * 2019年11月12日 12:07
 * Version 1.0
 */
public class DetailSimpleApi {
    /**
    * @param skuIds 商品ID，多个可以通过,分割，在这里使用只需要一个
     * @return
     * @throws ApiException
     */
    public static S_PlatformProductBean getDetail(String skuIds) throws JdException {
        JdClient client=new DefaultJdClient(CommUtil.jinfenUrl, null, CommUtil.appkey, CommUtil.secret);
        UnionOpenGoodsPromotiongoodsinfoQueryRequest request=new UnionOpenGoodsPromotiongoodsinfoQueryRequest();
        request.setSkuIds(skuIds);
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
            JSONObject responseObject = object.optJSONObject("jd_union_open_goods_promotiongoodsinfo_query_responce");
            String searchgoodsResult0 = responseObject.optString("queryResult");
            JSONObject searchgoodsResult = JSONObject.fromObject(searchgoodsResult0.substring(1, searchgoodsResult0.length() -1));
//            JSONObject searchgoodsResult = responseObject.optJSONObject("queryResult");
//            JSONObject resultObject = searchgoodsResult.optJSONObject("result");
            if(searchgoodsResult.optInt("code") != 200){
                throw new JdException("京东联盟网络连接异常");
            }
            JSONArray queryVos = searchgoodsResult.optJSONArray("data");
            if(queryVos.size() == 1) {

                //获取基本信息
                JSONObject baseInfoMap = BaseInfoApi.get(Long.parseLong(skuIds));
                JSONObject queryVo = queryVos.optJSONObject(0);
                product.setAuctionId(queryVo.optLong("skuId"));
                product.setCashBack(0d);
                product.setCashBackRatio(queryVo.optDouble("commisionRatioWl"));

                JSONArray imgs = baseInfoMap.optJSONArray("images");
                StringBuffer imgStr = new StringBuffer();
                if(imgs != null && imgs.size() > 0){
                    for(int i = 0; i < imgs.size(); i++){
                        imgStr.append(CommUtil.imgBasePath + imgs.get(i));
                        if(i < imgs.size() -1)
                            imgStr.append(";");
                    }
                }

                product.setImg(imgStr.toString());
                product.setPlatform(EnumUtil.ProductPlatformType.京东.value);
                product.setPrice(queryVo.optDouble("unitPrice"));
                product.setOldPrice(baseInfoMap.optDouble("mprice"));
                product.setTitle(queryVo.optString("goodsName"));
                product.setShopTitle(queryVo.optString("shopId"));
                product.setId(queryVo.optLong("skuId"));
                product.setSubtitle(queryVo.optString("cidName") + "-" + queryVo.optString("cid2Name") + "-" + queryVo.optString("cid3Name"));
                product.setShopTitle(baseInfoMap.optString("shopName"));
                product.setShareUrl(GetPromotionLongLinkApi.get(queryVo.optString("materialUrl")));
            }
//            throw new JdAccessTokenException(object.optString("msg"));
        } catch (Exception e) {
            e.printStackTrace();
            throw new JdException("京东联盟网络连接异常");
        }
        return product;
    }
}