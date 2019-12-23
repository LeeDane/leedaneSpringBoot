package com.cn.leedane.mall.taobao.api;

import com.cn.leedane.mall.model.RecommendResult;
import com.taobao.api.ApiException;

/**
 * 推荐商品的api
 * SearchMaterialApi
 * @author LeeDane
 * 2019年11月12日 12:07
 * Version 1.0
 */
public class RecommendProductApi {

    /**
     * 执行推荐商品（调用搜索物料实现）
     * @param productId 商品ID
     * @param count 获取数量
     * @return
     * @throws ApiException
     */
    public static RecommendResult recommend(long productId, long count) throws ApiException {
        return SearchMaterialApi.search(productId, count);
    }
}