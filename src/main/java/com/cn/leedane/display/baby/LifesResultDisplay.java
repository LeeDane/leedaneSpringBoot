package com.cn.leedane.display.baby;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import com.cn.leedane.display.keyValueDisplay;


/**
 * 宝宝首页生活方式返回对象展示的处理
 * @author LeeDane
 * 2018年6月12日 下午5:26:46
 * version 1.0
 */
public class LifesResultDisplay implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<Map<String, Object>> displays;
	
	private int eatNumber;
	
	private int sleepNumber;
	
	private int washNumber;
	
	private int sickNumber;
	
	/**
	 * 头部展示的发生时间字符串
	 */
	private String occurDate;
	
	/**
	 * echarts的option
	 */
	private JSONArray series;
	
	private List<String> xAxis;
	
	
	public List<Map<String, Object>> getDisplays() {
		return displays;
	}

	public void setDisplays(List<Map<String, Object>> displays) {
		this.displays = displays;
	}

	public int getEatNumber() {
		return eatNumber;
	}

	public void setEatNumber(int eatNumber) {
		this.eatNumber = eatNumber;
	}

	public int getSleepNumber() {
		return sleepNumber;
	}

	public void setSleepNumber(int sleepNumber) {
		this.sleepNumber = sleepNumber;
	}

	public int getWashNumber() {
		return washNumber;
	}

	public void setWashNumber(int washNumber) {
		this.washNumber = washNumber;
	}

	public int getSickNumber() {
		return sickNumber;
	}

	public void setSickNumber(int sickNumber) {
		this.sickNumber = sickNumber;
	}

	public String getOccurDate() {
		return occurDate;
	}

	public void setOccurDate(String occurDate) {
		this.occurDate = occurDate;
	}

	public JSONArray getSeries() {
		return series;
	}

	public void setSeries(JSONArray series) {
		this.series = series;
	}

	public List<String> getxAxis() {
		return xAxis;
	}

	public void setxAxis(List<String> xAxis) {
		this.xAxis = xAxis;
	}

	

}
