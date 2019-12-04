package com.cn.leedane.display.clock;

import java.io.Serializable;
import java.util.List;

/**
 * 任务列表提醒展示类bean
 * @author LeeDane
 * 2018年8月30日 下午3:28:24
 * version 1.0
 */
public class ClockDisplays implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<ClockDisplay> clockDisplays;

	public List<ClockDisplay> getClockDisplays() {
		return clockDisplays;
	}

	public void setClockDisplays(List<ClockDisplay> clockDisplays) {
		this.clockDisplays = clockDisplays;
	}
	
}
