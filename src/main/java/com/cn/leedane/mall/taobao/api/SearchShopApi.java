package com.cn.leedane.mall.taobao.api;

import com.cn.leedane.mall.model.SearchProductRequest;
import com.cn.leedane.mall.model.SearchProductResult;
import com.taobao.api.ApiException;

/**
 * 搜索商店的api
 * @link {https://open.taobao.com/api.htm?spm=a2e0r.13193907.0.0.542a24ad8JCoe6&docId=35896&docType=2}
 * @author LeeDane
 * 2019年11月12日 12:07
 * Version 1.0
 */
public class SearchShopApi {

    /**
     * 执行商品查询
     * @param productRequest
     * @return
     * @throws ApiException
     */
    public static SearchProductResult searchProduct(SearchProductRequest productRequest) throws ApiException {
        SearchProductResult searchProductResult = new SearchProductResult();
        return searchProductResult;
    }
}