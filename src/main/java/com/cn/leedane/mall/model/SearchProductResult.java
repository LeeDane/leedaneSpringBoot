package com.cn.leedane.mall.model;

import com.cn.leedane.model.mall.S_PlatformProductBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 封装商品查询结果集
 * @author LeeDane
 * 2019年11月26日 17:03
 * Version 1.0
 */
public class SearchProductResult {
    private int total;
    private List<S_PlatformProductBean> taobaoItems = new ArrayList<S_PlatformProductBean>();

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTotal() {
        return total;
    }

    public void setTaobaoItems(List<S_PlatformProductBean> taobaoItems) {
        this.taobaoItems = taobaoItems;
    }

    public List<S_PlatformProductBean> getTaobaoItems() {
        return taobaoItems;
    }
}
