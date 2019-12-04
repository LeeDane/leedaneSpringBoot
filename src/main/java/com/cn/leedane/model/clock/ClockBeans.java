package com.cn.leedane.model.clock;

import java.io.Serializable;
import java.util.List;

/**
 * 任务提醒列表实体bean
 * @author LeeDane
 * 2018年8月30日 下午3:15:36
 * version 1.0
 */
public class ClockBeans implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<ClockBean> clocks;

	public List<ClockBean> getClocks() {
		return clocks;
	}

	public void setClocks(List<ClockBean> clocks) {
		this.clocks = clocks;
	}
	
}
