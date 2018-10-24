package com.cn.leedane.display.clock;

import java.io.Serializable;
import java.util.List;

/**
 * 任务提醒分类展示组展示类bean
 * @author LeeDane
 * 2018年9月19日 下午5:49:18
 * version 1.0
 */
public class ClockDisplayGroups implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<ClockDisplayGroup> clockDisplayGroups;

	public List<ClockDisplayGroup> getClockDisplayGroups() {
		return clockDisplayGroups;
	}

	public void setClockDisplayGroups(List<ClockDisplayGroup> clockDisplayGroups) {
		this.clockDisplayGroups = clockDisplayGroups;
	}
}
