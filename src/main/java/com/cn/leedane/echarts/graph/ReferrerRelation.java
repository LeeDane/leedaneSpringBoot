package com.cn.leedane.echarts.graph;

import java.util.List;

/**
 * 推荐关系
 * @author LeeDane
 * 2020年01月04日 12:18
 * Version 1.0
 */
public class ReferrerRelation {
    private List<Node> nodes;
    private List<Link> links;

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }
}
