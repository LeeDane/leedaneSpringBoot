package com.cn.leedane.task.spring.scheduling;

/**
 * 基础的任务调度类
 * @author LeeDane
 * 2016年7月12日 下午3:24:14
 * Version 1.0
 */
public abstract class BaseScheduling {

	/**
	 * 是否启动任务调度
	 */
	private boolean open;
	
	public void setOpen(boolean open) {
		this.open = open;
	}
	
	protected void execute() throws Exception{
		
		//非开启就不往下面执行了
		if(!open){
			return;
		}
	};
	
}
