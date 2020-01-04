package com.cn.leedane.echarts.graph;

/**
 * @author LeeDane
 * 2020年01月04日 12:24
 * Version 1.0
 */
public class Link {
    private long id;
    private String name;
    private String source;
    private String target;
    private LineStyle lineStyle;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public LineStyle getLineStyle() {
        return lineStyle;
    }

    public void setLineStyle(LineStyle lineStyle) {
        this.lineStyle = lineStyle;
    }
}
