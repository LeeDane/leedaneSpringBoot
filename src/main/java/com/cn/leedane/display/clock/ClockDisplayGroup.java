package com.cn.leedane.display.clock;

import java.io.Serializable;
import java.util.List;

/**
 * 任务提醒分类展示组展示类bean
 * @author LeeDane
 * 2018年9月19日 下午5:49:18
 * version 1.0
 */
public class ClockDisplayGroup implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String name;
	
	private List<ClockDisplay> clockDisplays;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<ClockDisplay> getClockDisplays() {
		return clockDisplays;
	}

	public void setClockDisplays(List<ClockDisplay> clockDisplays) {
		this.clockDisplays = clockDisplays;
	}
}
