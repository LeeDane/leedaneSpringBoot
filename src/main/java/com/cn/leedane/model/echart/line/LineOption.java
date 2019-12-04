package com.cn.leedane.model.echart.line;

import java.io.Serializable;
import java.util.List;

import com.cn.leedane.model.echart.comm.Grid;
import com.cn.leedane.model.echart.comm.Legend;
import com.cn.leedane.model.echart.comm.Series;
import com.cn.leedane.model.echart.comm.Title;
import com.cn.leedane.model.echart.comm.Toolbox;
import com.cn.leedane.model.echart.comm.Tooltip;
import com.cn.leedane.model.echart.comm.XAxis;
import com.cn.leedane.model.echart.comm.YAxis;

/**
 * 折线图的option
 * @author LeeDane
 * 2017年12月24日 下午4:03:10
 * version 1.0
 */
public class LineOption implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Title title;
	
	private Tooltip tooltip;
	
	private Legend legend;
	
	private Grid grid;
	
	private Toolbox toolbox;
	
	private XAxis xAxis;
	
	private YAxis yAxis;
	
	private List<Series> series;

	public Title getTitle() {
		return title;
	}

	public void setTitle(Title title) {
		this.title = title;
	}

	public Tooltip getTooltip() {
		return tooltip;
	}

	public void setTooltip(Tooltip tooltip) {
		this.tooltip = tooltip;
	}

	public Legend getLegend() {
		return legend;
	}

	public void setLegend(Legend legend) {
		this.legend = legend;
	}

	public Grid getGrid() {
		return grid;
	}

	public void setGrid(Grid grid) {
		this.grid = grid;
	}

	public Toolbox getToolbox() {
		return toolbox;
	}

	public void setToolbox(Toolbox toolbox) {
		this.toolbox = toolbox;
	}

	public XAxis getxAxis() {
		return xAxis;
	}

	public void setxAxis(XAxis xAxis) {
		this.xAxis = xAxis;
	}

	public YAxis getyAxis() {
		return yAxis;
	}

	public void setyAxis(YAxis yAxis) {
		this.yAxis = yAxis;
	}

	public List<Series> getSeries() {
		return series;
	}

	public void setSeries(List<Series> series) {
		this.series = series;
	}
}
