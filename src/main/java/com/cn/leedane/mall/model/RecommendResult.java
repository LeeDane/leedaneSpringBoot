package com.cn.leedane.mall.model;

import com.cn.leedane.model.mall.S_PlatformProductBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 推荐商品的结果集合
 * @author LeeDane
 * 2019年12月16日 16:15
 * Version 1.0
 */
public class RecommendResult implements Serializable {
    private List<S_PlatformProductBean> items = new ArrayList<S_PlatformProductBean>();

    public List<S_PlatformProductBean> getItems() {
        return items;
    }

    public void setItems(List<S_PlatformProductBean> items) {
        this.items = items;
    }
}
