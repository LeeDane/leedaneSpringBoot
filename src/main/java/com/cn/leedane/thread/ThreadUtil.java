package com.cn.leedane.thread;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 线程任务
 * @author LeeDane
 * 2017年6月6日 下午6:09:50
 * version 1.0
 */
public class ThreadUtil{

	/**
	 * 执行单个任务
	 * @param task
	 */
	public void singleTask(Runnable task){
		Executor executor = Executors.newSingleThreadExecutor();
		executor.execute(task);
	}
	
}
