package com.cn.leedane.mall.taobao.api;

import com.cn.leedane.mall.taobao.CommUtil;
import com.cn.leedane.model.mall.S_PlatformProductBean;
import com.cn.leedane.model.mall.S_ProductBean;
import com.cn.leedane.model.mall.S_ShopBean;
import com.cn.leedane.utils.CollectionUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.MoneyUtil;
import com.cn.leedane.utils.StringUtil;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.TbkDgMaterialOptionalRequest;
import com.taobao.api.request.TbkItemInfoGetRequest;
import com.taobao.api.response.TbkDgMaterialOptionalResponse;
import com.taobao.api.response.TbkItemInfoGetResponse;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品详情(简单 )的api
 * @link {https://open.taobao.com/api.htm?spm=a2e0r.13193907.0.0.5b0824adxTmeHG&docId=24518&docType=2}
 * @author LeeDane
 * 2019年11月12日 12:07
 * Version 1.0
 */
public class DetailSimpleApi {

    /**
     * 通过获取详情(简版)的方式获取
     * @param itemId
     * @return
     * @throws ApiException
     */
    public static S_PlatformProductBean getDetailBySimple(String itemId) throws ApiException {
        S_ProductBean productBean = new S_ProductBean();
        TaobaoClient client = new DefaultTaobaoClient(CommUtil.url, CommUtil.appkey, CommUtil.secret);
        TbkItemInfoGetRequest req = new TbkItemInfoGetRequest();
        req.setNumIids(itemId);
//        req.setPlatform(1L);
//        req.setIp("11.22.33.43");
        TbkItemInfoGetResponse rsp = client.execute(req);
        String rspBody = rsp.getBody();
        if (StringUtil.isNull(rspBody))
            throw new ApiException("调用淘宝客api，结果返回空串");

        JSONObject resultObject = JSONObject.fromObject(rspBody);

        JSONObject errResponse = resultObject.optJSONObject("error_response");
        if(errResponse != null && !errResponse.isEmpty()){
            throw new ApiException(errResponse.optString("sub_msg"));
        }
        JSONObject responseObject = resultObject.optJSONObject("tbk_item_info_get_response");
        JSONObject resulObject = responseObject.optJSONObject("results");
        JSONArray items = resulObject.optJSONArray("n_tbk_item");
        if(CollectionUtil.isEmpty(items))
            return null;

        //只处理第一条信息，该接口暂时只支持获取一条信息
        return createBean(items.optJSONObject(0));
    }

    /**
     * 通过获取物料检索，传入商品ID的方式，只获取第一条记录的方式(目前没有发现问题)
    * @param itemId
     * @return
     * @throws ApiException
     */
    public static S_PlatformProductBean getDetailByMaterial(String itemId) throws ApiException {
        TaobaoClient client = new DefaultTaobaoClient(CommUtil.url, CommUtil.appkey, CommUtil.secret);
        TbkDgMaterialOptionalRequest req = new TbkDgMaterialOptionalRequest();
//		req.setStartDsr(10L);
        req.setPageSize(1L); //只获取第一条记录
        req.setSort("tk_rate_desc");
        req.setPageNo(1L); //只取第一页的数据
        req.setQ("https://detail.taobao.com/item.htm?id="+ itemId);
        req.setAdzoneId(CommUtil.adzoneId);
        TbkDgMaterialOptionalResponse rsp = client.execute(req);
        String rspBody = rsp.getBody();
        if (StringUtil.isNull(rspBody))
            throw new ApiException("调用淘宝客api，结果返回空串");

        JSONObject resultObject = JSONObject.fromObject(rspBody);

        JSONObject errResponse = resultObject.optJSONObject("error_response");
        if(errResponse != null && !errResponse.isEmpty()){
            throw new ApiException(errResponse.optString("sub_msg"));
        }
        JSONObject responseObject = resultObject.optJSONObject("tbk_dg_material_optional_response");
        JSONObject resultListObject = responseObject.optJSONObject("result_list");
        JSONArray mapDataArray = resultListObject.optJSONArray("map_data");
        return createBean(mapDataArray.optJSONObject(0));
    }

    /**
     * 根据S_ProductBean对象封装返回的信息
     * @param item
     * @return
     */
    private static S_PlatformProductBean createBean(JSONObject item){
        S_PlatformProductBean taobaoProductBean = new S_PlatformProductBean();

        S_ShopBean shop = new S_ShopBean();
        taobaoProductBean.setShopTitle(item.optString("nick"));

        StringBuffer imgStr = new StringBuffer();
        JSONObject smallImages = item.optJSONObject("small_images");
        if(smallImages != null){
            JSONArray imgs = smallImages.optJSONArray("string");

            if(imgs != null && imgs.size() > 0){
                for(int i = 0; i < imgs.size(); i++){
                    imgStr.append(imgs.get(i));
                    if(i < imgs.size() -1)
                        imgStr.append(";");
                }
            }
        }else{
            imgStr.append(item.optString("pict_url"));
        }

        //优惠券金额
        int couponAmount = item.optInt("coupon_amount");

        taobaoProductBean.setImg(imgStr.toString());
        taobaoProductBean.setPlatform(EnumUtil.ProductPlatformType.淘宝.value);
        taobaoProductBean.setPrice(item.optDouble("zk_final_price"));
        taobaoProductBean.setOldPrice(item.optDouble("reserve_price"));
        taobaoProductBean.setCashBack((item.optDouble("commission_rate") / 100 * (item.optDouble("zk_final_price") - couponAmount)));
        taobaoProductBean.setCashBackRatio(item.optDouble("commission_rate") / 100);
        taobaoProductBean.setCouponAmount(couponAmount);
        taobaoProductBean.setCouponShareUrl(item.optString("coupon_share_url"));
        taobaoProductBean.setTitle(item.optString("title"));
        taobaoProductBean.setId(item.optLong("num_iid"));
        taobaoProductBean.setAfterCouponPrice(MoneyUtil.twoDecimalPlaces(taobaoProductBean.getPrice() - couponAmount));
        taobaoProductBean.setShareUrl(item.optString("url"));
        taobaoProductBean.setSubtitle(item.optString("short_title"));
        taobaoProductBean.setDetail(item.optString("item_description"));
        taobaoProductBean.setClickId("tb_"+ item.optLong("num_iid"));
        taobaoProductBean.setSales("30天售量:"+ item.optLong("volume"));
        return taobaoProductBean;
    }
}