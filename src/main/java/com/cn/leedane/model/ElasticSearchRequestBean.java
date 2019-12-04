package com.cn.leedane.model;

import org.elasticsearch.search.sort.SortOrder;

/**
 * ElasticSearch请求实体bean(主要目的是参数太多了)
 * @author LeeDane
 * 2019年7月9日 下午18:42:52
 * Version 1.0
 */
public class ElasticSearchRequestBean {
    private String table; //不能为空， 业务表名称，相应创建的表会在后面添加index_
    private String[] searchFields; //不能为空，需要检索的字段
    private String searchKey; //不能为空，检索的关键字
    private int accurate; //不能为空，检索类型：0 模糊查询， 1 精确查询 2前缀查询
    private String sortField; //可以为空， 排序字段
    private SortOrder order; //可以为空，排序域
    private int start; //不能为空，默认是0
    private int number; //不能为空，默认必须是大于等于0， 为0表示只获取统计数，不获取详细的文档数据
    private String[] showFields; //可以为空，只返回指定的字段，默认展示所有的字段，
    private UserBean user; //当前操作用户(主要用于权限的判断，未登录用户只能查询正常状态下的信息，非管理员，只能查询公开的或者自己的私有的，管理员可以查看所有的数据)

    private String startDate; //开始日期
    private String endDate; //结束日期
    private int searchType; //查询的类型
    public String[] getSearchFields() {
        return searchFields;
    }

    public void setSearchFields(String[] searchFields) {
        this.searchFields = searchFields;
    }

    public String getSearchKey() {
        return searchKey;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

    public int getAccurate() {
        return accurate;
    }

    public void setAccurate(int accurate) {
        this.accurate = accurate;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public SortOrder getOrder() {
        return order;
    }

    public void setOrder(SortOrder order) {
        this.order = order;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String[] getShowFields() {
        return showFields;
    }

    public void setShowFields(String[] showFields) {
        this.showFields = showFields;
    }

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getSearchType() {
        return searchType;
    }

    public void setSearchType(int searchType) {
        this.searchType = searchType;
    }
}
