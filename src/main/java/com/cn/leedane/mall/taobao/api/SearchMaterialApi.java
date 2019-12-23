package com.cn.leedane.mall.taobao.api;

import com.cn.leedane.mall.model.RecommendResult;
import com.cn.leedane.mall.taobao.CommUtil;
import com.cn.leedane.model.mall.S_PlatformProductBean;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.StringUtil;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.TbkDgOptimusMaterialRequest;
import com.taobao.api.response.TbkDgOptimusMaterialResponse;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 物料搜索的api
 * @link {https://open.taobao.com/api.htm?docId=33947&docType=2}
 * @author LeeDane
 * 2019年11月12日 12:07
 * Version 1.0
 */
public class SearchMaterialApi {

    /**
     * 执行推荐商品
     * @param productId 商品ID
     * @param count 获取数量
     * @return
     * @throws ApiException
     */
    public static RecommendResult search(long productId, long count) throws ApiException {
        RecommendResult recommendResult = new RecommendResult();
        TaobaoClient client = new DefaultTaobaoClient(CommUtil.url, CommUtil.appkey, CommUtil.secret);
        TbkDgOptimusMaterialRequest req = new TbkDgOptimusMaterialRequest();
        req.setPageSize(count);
        req.setAdzoneId(CommUtil.adzoneId);
        req.setPageNo(1L);
        req.setMaterialId(13256L); //相似推荐物料ID,目前是固定值
		/*req.setDeviceValue("xxx");
		req.setDeviceEncrypt("MD5");
		req.setDeviceType("IMEI");*/
        //req.setContentId(323L);
        //req.setContentSource("xxx");
        req.setItemId(productId);
        TbkDgOptimusMaterialResponse rsp = client.execute(req);
        String rspBody = rsp.getBody();
        if (StringUtil.isNull(rspBody))
            throw new ApiException("调用淘宝客api，结果返回空串");

        JSONObject resultObject = JSONObject.fromObject(rspBody);

        JSONObject errResponse = resultObject.optJSONObject("error_response");
        if(errResponse != null && !errResponse.isEmpty()){
            throw new ApiException(errResponse.optString("sub_msg"));
        }
        JSONObject responseObject = resultObject.optJSONObject("tbk_dg_optimus_material_response");
        JSONObject resultListObject = responseObject.optJSONObject("result_list");
        if(resultListObject == null)
            return recommendResult;
        JSONArray mapDataArray = resultListObject.optJSONArray("map_data");
        List<S_PlatformProductBean> taobaoItems = new ArrayList<S_PlatformProductBean>();
        for (int i = 0; i < mapDataArray.size(); i++) {
            S_PlatformProductBean taobaoProductBean = new S_PlatformProductBean();
            JSONObject item = mapDataArray.optJSONObject(i);
            taobaoProductBean.setTitle(item.optString("title"));
            taobaoProductBean.setId(item.optLong("item_id"));
            taobaoProductBean.setAuctionId(item.optLong("item_id"));
            taobaoProductBean.setImg(item.optString("pict_url"));
            taobaoProductBean.setOldPrice(item.optDouble("zk_final_price"));
            taobaoProductBean.setPrice(item.optDouble("zk_final_price") - item.optDouble("coupon_amount"));
            taobaoProductBean.setPlatform(EnumUtil.ProductPlatformType.淘宝.value);
            taobaoProductBean.setCouponShareUrl(item.optString("coupon_share_url"));
            taobaoProductBean.setShopTitle(item.optString("shop_title"));
            taobaoProductBean.setClickId("tb_"+ item.optLong("item_id"));
            taobaoItems.add(taobaoProductBean);
        }
        //设置结果集
        recommendResult.setItems(taobaoItems);
        return recommendResult;
    }
}