package com.cn.leedane.task.spring.scheduling;

import org.quartz.SchedulerException;

/**
 * 基础的任务调度类
 * @author LeeDane
 * 2016年7月12日 下午3:24:14
 * Version 1.0
 */
public interface BaseScheduling {
	
	
	/**
	 * 执行任务的操作
	 * @throws SchedulerException
	 */
	public void execute() throws SchedulerException ;
	
}
