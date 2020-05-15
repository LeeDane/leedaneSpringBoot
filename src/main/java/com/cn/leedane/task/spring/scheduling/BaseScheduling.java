package com.cn.leedane.task.spring.scheduling;

import java.util.Map;

import com.cn.leedane.model.JobManageBean;
import org.quartz.SchedulerException;

/**
 * 基础的任务调度类
 * @author LeeDane
 * 2016年7月12日 下午3:24:14
 * Version 1.0
 */
public interface BaseScheduling {
	
	/**
	 * 获取参数
	 * @return
	 */
	public Map<String, Object> getParams();
	
	/**
	 * 设置参数
	 */
	public void setParams(String params);

	/**
	 * 设置任务的bean
	 * @return
	 */
	public void setJobBean(JobManageBean jobManageBean);

	/**
	 * 获取任务的bean
	 * @return
	 */
	public JobManageBean getJobBean();

	/**
	 * 执行任务的操作
	 * @throws SchedulerException
	 */
	public void execute() throws SchedulerException ;
	
}
