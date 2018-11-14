package com.cn.leedane.model.clock;

import java.io.Serializable;

/**
 * 任务积分在rabbitmq队列中的实体类
 * @author LeeDane
 * 2018年11月5日 下午4:46:31
 * version 1.0
 */
public class ClockScoreQueueBean implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ClockScoreBean clockScoreBean; //积分任务
	private int operateType; //操作类型， 1：新增， 2：修改， 3：删除
	public int getOperateType() {
		return operateType;
	}
	public void setOperateType(int operateType) {
		this.operateType = operateType;
	}
	public ClockScoreBean getClockScoreBean() {
		return clockScoreBean;
	}
	public void setClockScoreBean(ClockScoreBean clockScoreBean) {
		this.clockScoreBean = clockScoreBean;
	}
	
	
}
