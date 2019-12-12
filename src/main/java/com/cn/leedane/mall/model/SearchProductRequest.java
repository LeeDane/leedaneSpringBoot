package com.cn.leedane.mall.model;

/**
 * 封装商品查询请求参数
 * @author LeeDane
 * 2019年11月26日 19:27
 * Version 1.0
 */
public class SearchProductRequest {
    private long pageSize;
    private long pageNo;
    private String keyword;
    private String sort; //排序的信息

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }

    public long getPageSize() {
        return pageSize;
    }

    public void setPageSize(long pageSize) {
        this.pageSize = pageSize;
    }

    public long getPageNo() {
        return pageNo;
    }

    public void setPageNo(long pageNo) {
        this.pageNo = pageNo;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }
}
