package com.cn.leedane.model.clock;

import com.cn.leedane.model.BaseQueueBean;
import com.cn.leedane.model.UserBean;

/**
 * 任务动态在rabbitmq队列中的实体类
 * @author LeeDane
 * 2018年11月26日 下午3:54:06
 * version 1.0
 */
public class ClockDynamicQueueBean extends BaseQueueBean{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private ClockDynamicBean clockDynamicBean; //任务动态
	
//	private UserBean user; //用户信息

	public ClockDynamicBean getClockDynamicBean() {
		return clockDynamicBean;
	}

	public void setClockDynamicBean(ClockDynamicBean clockDynamicBean) {
		this.clockDynamicBean = clockDynamicBean;
	}
	
}
