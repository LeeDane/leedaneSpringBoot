package com.cn.leedane.model;

import java.io.Serializable;

/**
 * @author LeeDane
 * 2020年04月15日 18:42
 * Version 1.0
 */
public class LogoutTable implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -4775876471761087890L;

    private String table;
    private String field;
    private int order;

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
