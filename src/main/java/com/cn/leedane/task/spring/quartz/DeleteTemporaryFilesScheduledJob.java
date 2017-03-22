package com.cn.leedane.task.spring.quartz;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.cn.leedane.task.spring.scheduling.DeleteTemporaryFiles;

/**
 * 删除临时文件的调度任务
 * @author LeeDane
 * 2016年7月12日 下午3:13:41
 * Version 1.0
 */
public class DeleteTemporaryFilesScheduledJob extends QuartzJobBean {

	private DeleteTemporaryFiles deleteTemporaryFiles; 

	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {
		try {
			//deleteTemporaryFiles.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setDeleteTemporaryFiles(
			DeleteTemporaryFiles deleteTemporaryFiles) {
		this.deleteTemporaryFiles = deleteTemporaryFiles;
	}
}
