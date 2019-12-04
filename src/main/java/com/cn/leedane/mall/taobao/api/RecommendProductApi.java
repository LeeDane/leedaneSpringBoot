package com.cn.leedane.mall.taobao.api;

import com.cn.leedane.mall.taobao.CommUtil;
import com.cn.leedane.model.mall.S_PlatformProductBean;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.StringUtil;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.TbkItemRecommendGetRequest;
import com.taobao.api.response.TbkItemRecommendGetResponse;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 推荐商品的api
 * @link {https://open.taobao.com/api.htm?spm=a2e0r.13193907.0.0.2f7f24adbId0KM&docId=24517&docType=2}
 * @author LeeDane
 * 2019年11月12日 12:07
 * Version 1.0
 */
public class RecommendProductApi {

    /**
     * 执行推荐商品
     * @param productId 商品ID
     * @param count 获取数量
     * @return
     * @throws ApiException
     */
    public static RecommendProductResult recommend(long productId, long count) throws ApiException {
        RecommendProductResult searchProductResult = new RecommendProductResult();
        TaobaoClient client = new DefaultTaobaoClient(CommUtil.url, CommUtil.appkey, CommUtil.secret);
        TbkItemRecommendGetRequest req = new TbkItemRecommendGetRequest();
        //完整的字段num_iid,title,pict_url,small_images,reserve_price,zk_final_price,user_type,provcity,item_url
        req.setFields("num_iid,title,pict_url,reserve_price,zk_final_price,user_type,provcity,item_url");
        req.setNumIid(productId);
        req.setCount(count);
        req.setPlatform(1L);
        TbkItemRecommendGetResponse rsp = client.execute(req);
        String rspBody = rsp.getBody();
        if (StringUtil.isNull(rspBody))
            throw new ApiException("调用淘宝客api，结果返回空串");

        JSONObject resultObject = JSONObject.fromObject(rspBody);

        JSONObject errResponse = resultObject.optJSONObject("error_response");
        if(errResponse != null && !errResponse.isEmpty()){
            throw new ApiException(errResponse.optString("sub_msg"));
        }
        JSONObject responseObject = resultObject.optJSONObject("tbk_item_recommend_get_response");
        JSONObject resultListObject = responseObject.optJSONObject("results");
        if(resultListObject == null)
            return searchProductResult;
        JSONArray mapDataArray = resultListObject.optJSONArray("n_tbk_item");
        List<S_PlatformProductBean> taobaoItems = new ArrayList<S_PlatformProductBean>();
        for (int i = 0; i < mapDataArray.size(); i++) {
            S_PlatformProductBean productBean = new S_PlatformProductBean();
            JSONObject item = mapDataArray.optJSONObject(i);
            productBean.setTitle(item.optString("title"));
            productBean.setId(item.optLong("num_iid"));
            productBean.setImg(item.optString("pict_url"));
            productBean.setOldPrice(item.optDouble("reserve_price"));
            productBean.setPrice(item.optDouble("zk_final_price"));
            productBean.setPlatform(EnumUtil.ProductPlatformType.淘宝.value);
            taobaoItems.add(productBean);
        }
        //设置结果集
        searchProductResult.setTaobaoItems(taobaoItems);
        return searchProductResult;
    }

    /**
     * 封装商品推荐查询结果集
     */
    public static class RecommendProductResult{
        private List<S_PlatformProductBean> taobaoItems = new ArrayList<S_PlatformProductBean>();

        public void setTaobaoItems(List<S_PlatformProductBean> taobaoItems) {
            this.taobaoItems = taobaoItems;
        }

        public List<S_PlatformProductBean> getTaobaoItems() {
            return taobaoItems;
        }
    }
}